package com.example.idempotence;

import com.example.idempotence.idempotent.filter.ParameterFilterException;
import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.agents.redis.RedisAgent;
import com.example.idempotence.idempotent.annotations.Idempotent;
import com.example.idempotence.idempotent.hash.HashingStrategy;
import com.example.idempotence.idempotent.hash.HashingStrategySelector;
import com.example.idempotence.idempotent.payload.IdempotentPayload;
import com.example.idempotence.idempotent.payload.IdempotentPayloadSerializer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class IdempotenceAspect {

    private final IdempotentAgent idempotentAgent = new RedisAgent("localhost", 6379);
    // private final IdempotentAgent idempotentAgent = new MemoryAgent();

    @Around("@annotation(com.example.idempotence.idempotent.annotations.Idempotent)")
    public Object assertIdempotence(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Idempotent idempotentAnnotation = method.getAnnotation(Idempotent.class);
        final String strategyString = idempotentAnnotation.strategy();
        final List<String> includes = Arrays.asList(idempotentAnnotation.include());
        final List<String> excludes = Arrays.asList(idempotentAnnotation.exclude());

        final List<String> parameterNames = Arrays.asList(signature.getParameterNames());
        final Object[] allArgs = joinPoint.getArgs();

        Object[] usedArgs = filter(includes, excludes, parameterNames, allArgs);

        if (usedArgs.length == 0) {
            System.out.println("This code will always be executed");
            return joinPoint.proceed();
        }

        final HashingStrategy hashingStrategy = HashingStrategySelector.select(strategyString);
        String hash = hashingStrategy.calculateHash(usedArgs);
        if (idempotentAgent.executed(hash)) {
            System.out.println("Object already executed");
            byte[] returnValue = idempotentAgent.read(hash);
            final IdempotentPayload returnPayload = IdempotentPayloadSerializer.deserialize(returnValue);
            return returnPayload.getReturnObject();
        }

        final Object returnValue = joinPoint.proceed();
        byte[] payload = IdempotentPayloadSerializer.serialize(buildIdempotentPayload(returnValue));
        idempotentAgent.save(hash, payload, 10);

        return returnValue;
    }

    private Object[] filter(
            final List<String> includes,
            final List<String> excludes,
            final List<String> parameterNames,
            final Object[] args
    ) throws ParameterFilterException {
        if (containsFilterValues(includes)) {
            return processInclusion(includes, parameterNames, args);
        }
        if (containsFilterValues(excludes)) {
            return processExclusion(excludes, parameterNames, args);
        }

        return args;
    }

    private Object[] processInclusion(
            final List<String> filterValues,
            final List<String> parameters,
            Object[] args
    ) throws ParameterFilterException {
        assertFilterValuesAreValid(filterValues, parameters);

        List<Object> selectedArgs = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if (filterValues.contains(parameters.get(i))) {
                selectedArgs.add(args[i]);
            }
        }

        return selectedArgs.toArray();
    }

    private Object[] processExclusion(
            final List<String> filterValues,
            final List<String> parameters,
            Object[] args
    ) throws ParameterFilterException {
        assertFilterValuesAreValid(filterValues, parameters);

        List<Object> selectedArgs = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if (!filterValues.contains(parameters.get(i))) {
                selectedArgs.add(args[i]);
            }
        }

        return selectedArgs.toArray();
    }

    private void assertFilterValuesAreValid(final List<String> filterValues, final List<String> parameters)
            throws ParameterFilterException {
        for (final String filterValue : filterValues) {
            if (!parameters.contains(filterValue)) {
                final String message = "filter '" + filterValue + "' on include/exclude "
                        + "has no correspondence with function parameters";
                throw new ParameterFilterException(message);
            }
        }
    }

    private boolean containsFilterValues(final List<String> filterValues) {
        return 1 != filterValues.size() || !"".equals(filterValues.get(0));
    }

    private IdempotentPayload buildIdempotentPayload(final Object returnObject) {
        return new IdempotentPayload(false, (Serializable) returnObject);
    }
}
