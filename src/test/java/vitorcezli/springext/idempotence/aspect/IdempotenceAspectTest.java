package vitorcezli.springext.idempotence.aspect;

import vitorcezli.springext.idempotence.agents.IdempotenceAgent;
import vitorcezli.springext.idempotence.agents.memory.MemoryAgent;
import vitorcezli.springext.idempotence.configuration.IdempotenceProps;
import vitorcezli.springext.idempotence.filter.ParameterFilterException;
import vitorcezli.springext.idempotence.hash.HashingStrategyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdempotenceAspectTest {

    private MockService serviceProxy;

    @BeforeEach
    public void setUp() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        final PropSelector propSelector = new PropSelector(idempotenceProps);
        final IdempotenceAgent idempotenceAgent = new MemoryAgent();
        final IdempotenceAspect idempotenceAspect = new IdempotenceAspect(propSelector,
                idempotenceAgent);

        final AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new MockService());
        aspectJProxyFactory.addAspect(idempotenceAspect);

        final DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        final AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        serviceProxy = (MockService) aopProxy.getProxy();
    }

    @Test
    @DisplayName("Should execute function correctly")
    public void shouldExecuteFunction() {
        serviceProxy.resetExecution();
        final String value = "value";
        final String returnValue = serviceProxy.getSomething(value);
        assertEquals(value, returnValue);
        assertTrue(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should not execute function twice")
    public void shouldNotExecuteFunctionTwice() {
        final String value = "value";
        serviceProxy.getSomething(value);

        serviceProxy.resetExecution();
        serviceProxy.getSomething(value);
        assertFalse(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should execute function if another value is passed on parameter")
    public void shouldExecuteFunctionForDifferentParameter() {
        serviceProxy.getSomething("value");

        serviceProxy.resetExecution();
        serviceProxy.getSomething("otherValue");
        assertTrue(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should not execute the same function if inclusion value is present")
    public void shouldExecuteNotFunctionForInclusion() {
        serviceProxy.include("value1", "value2");

        serviceProxy.resetExecution();
        serviceProxy.include("value1", "value3");
        assertFalse(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should return the same value when idempotence is assured on inclusion")
    public void shouldReturnTheSameValueOnInclusion() {
        serviceProxy.include("value1", "value2");

        serviceProxy.resetExecution();
        final String returnValue = serviceProxy.include("value1", "value3");
        assertEquals("value1value2", returnValue);
    }

    @Test
    @DisplayName("Should execute the same function if inclusion value is not present")
    public void shouldExecuteFunctionForInclusion() {
        serviceProxy.include("value1", "value2");

        serviceProxy.resetExecution();
        serviceProxy.include("value3", "value2");
        assertTrue(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should return the last value when idempotence is not assured on inclusion")
    public void shouldReturnDifferentValueOnInclusion() {
        serviceProxy.include("value1", "value2");

        serviceProxy.resetExecution();
        final String returnValue = serviceProxy.include("value3", "value2");
        assertEquals("value3value2", returnValue);
    }

    @Test
    @DisplayName("Should not execute function if value left after exclusion is present")
    public void shouldNotExecuteFunctionForExclusion() {
        serviceProxy.exclude("value1", "value2");

        serviceProxy.resetExecution();
        serviceProxy.exclude("value3", "value2");
        assertFalse(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should return the same value when idempotence is assured on exclusion")
    public void shouldReturnTheSameValueOnExclusion() {
        serviceProxy.exclude("value1", "value2");

        serviceProxy.resetExecution();
        final String returnValue = serviceProxy.exclude("value3", "value2");
        assertEquals("value1value2", returnValue);
    }

    @Test
    @DisplayName("Should not execute function if value left after exclusion is not present")
    public void shouldExecuteFunctionForExclusion() {
        serviceProxy.exclude("value1", "value2");

        serviceProxy.resetExecution();
        serviceProxy.exclude("value3", "value1");
        assertTrue(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should return the last value when idempotence is not assured on exclusion")
    public void shouldReturnDifferentValueOnExclusion() {
        serviceProxy.exclude("value1", "value2");

        serviceProxy.resetExecution();
        final String returnValue = serviceProxy.exclude("value3", "value3");
        assertEquals("value3value3", returnValue);
    }

    @Test
    @DisplayName("Should raise ParameterFilterException for invalid excludes")
    public void shouldRaiseParameterFilterException() {
        assertThrows(ParameterFilterException.class, () -> serviceProxy.invalidExcludes("value"));
    }

    @Test
    @DisplayName("Should raise ParameterFilterException if both 'include' and 'exclude' are defined")
    public void shouldRaiseParameterFilterException2() {
        assertThrows(ParameterFilterException.class, () -> serviceProxy.includeAndExclude("value"));
    }

    @Test
    @DisplayName("Should not execute function if ParameterFilterException is raised")
    public void shouldNotExecuteIfParameterFilterException() {
        serviceProxy.resetExecution();
        assertThrows(ParameterFilterException.class, () -> serviceProxy.invalidExcludes("value"));
        assertFalse(serviceProxy.getExecuted());
    }

    @Test
    @DisplayName("Should raise HashingStrategyException for invalid strategy")
    public void shouldRaiseHashingStrategyException() {
        assertThrows(HashingStrategyException.class, () -> serviceProxy.invalidHashing("value"));
    }

    @Test
    @DisplayName("Should not execute function if HashingStrategyException is raised")
    public void shouldNotExecuteIfHashingStrategyException() {
        serviceProxy.resetExecution();
        assertThrows(HashingStrategyException.class, () -> serviceProxy.invalidHashing("value"));
        assertFalse(serviceProxy.getExecuted());
    }
}