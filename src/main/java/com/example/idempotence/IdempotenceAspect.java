package com.example.idempotence;

import com.example.idempotence.idempotent.configuration.IdempotenceProps;
import com.example.idempotence.idempotent.filter.ParameterFilter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class IdempotenceAspect {

    private final IdempotenceProps idempotenceProps;
    private final IdempotentAgent idempotentAgent;

    @Autowired
    public IdempotenceAspect(final IdempotenceProps idempotenceProps, final IdempotentAgent idempotentAgent) {
        this.idempotenceProps = idempotenceProps;
        this.idempotentAgent = idempotentAgent;
    }

    @Around("@annotation(com.example.idempotence.idempotent.annotations.Idempotent)")
    public Object assertIdempotence(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Idempotent idempotentAnnotation = method.getAnnotation(Idempotent.class);
        String strategyString = idempotentAnnotation.strategy();
        if (strategyString.length() == 0) {
            strategyString = idempotenceProps.getHash();
        }

        final List<String> includes = Arrays.asList(idempotentAnnotation.include());
        final List<String> excludes = Arrays.asList(idempotentAnnotation.exclude());

        final List<String> parameterNames = Arrays.asList(signature.getParameterNames());
        final Object[] allArgs = joinPoint.getArgs();

        Object[] usedArgs = ParameterFilter.filter(includes, excludes, parameterNames, allArgs);

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
        idempotentAgent.save(hash, payload, idempotenceProps.getTtl());

        return returnValue;
    }

    private IdempotentPayload buildIdempotentPayload(final Object returnObject) {
        return new IdempotentPayload(false, (Serializable) returnObject);
    }
}
