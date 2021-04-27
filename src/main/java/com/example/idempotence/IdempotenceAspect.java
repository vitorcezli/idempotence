package com.example.idempotence;

import com.example.idempotence.idempotent.configuration.IdempotenceProps;
import com.example.idempotence.idempotent.filter.ParameterFilter;
import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.annotations.Idempotent;
import com.example.idempotence.idempotent.hash.HashingStrategy;
import com.example.idempotence.idempotent.hash.HashingStrategyException;
import com.example.idempotence.idempotent.hash.HashingStrategySelector;
import com.example.idempotence.idempotent.logging.IdempotenceLogger;
import com.example.idempotence.idempotent.payload.PayloadSerializer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class IdempotenceAspect {

    private final IdempotenceProps idempotenceProps;
    private final IdempotentAgent idempotentAgent;
    private final IdempotenceLogger idempotenceLogger;

    @Autowired
    public IdempotenceAspect(final IdempotenceProps idempotenceProps, final IdempotentAgent idempotentAgent) {
        this.idempotenceProps = idempotenceProps;
        this.idempotentAgent = idempotentAgent;
        this.idempotenceLogger = new IdempotenceLogger(idempotenceProps.getLogging());
    }

    @Around("@annotation(com.example.idempotence.idempotent.annotations.Idempotent)")
    public Object assertIdempotence(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        final String source = signature.getDeclaringTypeName() + "." + method.getName();
        idempotenceLogger.logStart(source);

        Idempotent idempotentAnnotation = method.getAnnotation(Idempotent.class);

        final List<String> includes = Arrays.asList(idempotentAnnotation.include());
        final List<String> excludes = Arrays.asList(idempotentAnnotation.exclude());

        final List<String> parameterNames = Arrays.asList(signature.getParameterNames());
        final Object[] allArgs = joinPoint.getArgs();

        Object[] usedArgs = ParameterFilter.filter(includes, excludes, parameterNames, allArgs);

        if (usedArgs.length == 0) {
            idempotenceLogger.logAlwaysExecuted(source);
            return joinPoint.proceed();
        }

        final HashingStrategy hashingStrategy = getHashingStrategy(idempotentAnnotation);
        String hash = hashingStrategy.calculateHash(source, usedArgs);

        byte[] returnValue = idempotentAgent.read(hash);
        if (null != returnValue) {
            idempotenceLogger.logExisting(source);
            idempotenceLogger.logEnd(source);
            return PayloadSerializer.deserialize(returnValue);
        } else {
            idempotenceLogger.logNew(source);
        }

        final Object returnedObject = joinPoint.proceed();
        idempotenceLogger.logExecution(source, getTtl(idempotentAnnotation));
        byte[] payload = PayloadSerializer.serialize((Serializable) returnedObject);
        idempotentAgent.save(hash, payload, getTtl(idempotentAnnotation));

        idempotenceLogger.logEnd(source);
        return returnedObject;
    }

    private HashingStrategy getHashingStrategy(final Idempotent idempotent)
            throws HashingStrategyException {
        String strategyString = idempotent.strategy();
        if (strategyString.length() == 0) {
            strategyString = idempotenceProps.getHash();
        }

        return HashingStrategySelector.select(strategyString);
    }

    private int getTtl(final Idempotent idempotent) {
        int ttl = idempotent.ttl();
        if (ttl < 0) {
            ttl = idempotenceProps.getTtl();
        }

        return ttl;
    }
}
