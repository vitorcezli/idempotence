package io.github.vitorcezli.idempotence.aspect;

import io.github.vitorcezli.idempotence.configuration.IdempotenceProps;
import io.github.vitorcezli.idempotence.annotations.Idempotent;
import io.github.vitorcezli.idempotence.hash.HashingStrategy;
import io.github.vitorcezli.idempotence.hash.HashingStrategyException;
import io.github.vitorcezli.idempotence.hash.implementations.HashCodeStrategy;
import io.github.vitorcezli.idempotence.hash.implementations.ToStringStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropSelectorTest {

    @Test
    @DisplayName("Should equal the logging value of idempotenceProps object")
    void shouldEqualLoggingProps1() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setLogging(true);

        final PropSelector propSelector = new PropSelector(idempotenceProps);
        assertTrue(propSelector.getLogging());
    }

    @Test
    @DisplayName("Should equal the logging value of idempotenceProps object")
    void shouldEqualLoggingProps2() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setLogging(false);

        final PropSelector propSelector = new PropSelector(idempotenceProps);
        assertFalse(propSelector.getLogging());
    }

    @Test
    @DisplayName("Should equal the hashing value of idempotenceProps object")
    void shouldEqualHashingProps() throws HashingStrategyException {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setHash("hashCode");

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("", 0);
        final HashingStrategy hashingStrategy = propSelector.getHashingStrategy(idempotent);
        assertTrue(hashingStrategy instanceof HashCodeStrategy);
    }

    @Test
    @DisplayName("Should equal the hashing value of Idempotent annotation object")
    void shouldEqualHashingAnnotation() throws HashingStrategyException {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setHash("hashCode");

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("toString", 0);
        final HashingStrategy hashingStrategy = propSelector.getHashingStrategy(idempotent);
        assertTrue(hashingStrategy instanceof ToStringStrategy);
    }

    @Test
    @DisplayName("Should throw HashingStrategyException for invalid value on Idempotent annotation object")
    void shouldThrowHashingStrategyException() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setHash("hashCode");

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("invalid", 0);
        assertThrows(HashingStrategyException.class, () -> propSelector.getHashingStrategy(idempotent));
    }

    @Test
    @DisplayName("Should equal the TTL value of idempotenceProps object")
    void shouldEqualTtlProps() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setTtl(10);

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("toString", -1);
        assertEquals(10, propSelector.getTtl(idempotent));
    }

    @Test
    @DisplayName("Should equal the TTL value of Idempotent annotation object")
    void shouldEqualTtlAnnotation() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setTtl(10);

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("toString", 11);
        assertEquals(11, propSelector.getTtl(idempotent));
    }

    @Test
    @DisplayName("Should equal the TTL value of idempotenceProps object for a invalid TTL on annotation")
    void shouldEqualDefaultTtl() {
        final IdempotenceProps idempotenceProps = new IdempotenceProps();
        idempotenceProps.setTtl(10);

        final PropSelector propSelector = new PropSelector(idempotenceProps);

        final Idempotent idempotent = generateIdempotentAnnotationObject("toString", -2);
        assertEquals(10, propSelector.getTtl(idempotent));
    }

    private Idempotent generateIdempotentAnnotationObject(final String strategy, final int ttl) {
        return new Idempotent() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Idempotent.class;
            }

            @Override
            public String[] include() {
                return new String[]{""};
            }

            @Override
            public String[] exclude() {
                return new String[]{""};
            }

            @Override
            public String strategy() {
                return strategy;
            }

            @Override
            public int ttl() {
                return ttl;
            }
        };
    }
}