package io.github.vitorcezli.idempotence.aspect;

import io.github.vitorcezli.idempotence.agents.IdempotenceAgent;
import io.github.vitorcezli.idempotence.hash.HashingStrategy;
import io.github.vitorcezli.idempotence.annotations.Idempotent;
import io.github.vitorcezli.idempotence.filter.ParameterFilter;
import io.github.vitorcezli.idempotence.filter.ParameterFilterException;
import io.github.vitorcezli.idempotence.logging.IdempotenceLogger;
import io.github.vitorcezli.idempotence.payload.PayloadSerializer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class IdempotenceAspect {

    private final PropSelector propSelector;
    private final IdempotenceAgent idempotenceAgent;
    private final IdempotenceLogger idempotenceLogger;

    @Autowired
    public IdempotenceAspect(final PropSelector propSelector, final IdempotenceAgent idempotenceAgent) {
        this.propSelector = propSelector;
        this.idempotenceAgent = idempotenceAgent;
        this.idempotenceLogger = new IdempotenceLogger(propSelector.getLogging());
    }

    @Around("@annotation(io.github.vitorcezli.idempotence.annotations.Idempotent)")
    public Object assertIdempotence(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String source = extractSource(joinPoint);
        idempotenceLogger.logStart(source);

        final Object[] usedArgs = getValidArguments(joinPoint);
        final Idempotent idempotentAnnotation = extractAnnotation(joinPoint);

        if (usedArgs.length == 0) {
            idempotenceLogger.logAlwaysExecuted(source);
            return joinPoint.proceed();
        }

        final HashingStrategy hashingStrategy = propSelector.getHashingStrategy(idempotentAnnotation);
        final String hash = hashingStrategy.calculateHash(source, usedArgs);
        final byte[] returnValue = idempotenceAgent.read(hash);

        if (null != returnValue) {
            idempotenceLogger.logExisting(source);
            idempotenceLogger.logEnd(source);
            return PayloadSerializer.deserialize(returnValue);
        }

        idempotenceLogger.logNew(source);
        final int ttl = propSelector.getTtl(idempotentAnnotation);
        final Object returnedObject = joinPoint.proceed();
        idempotenceLogger.logExecution(source, ttl);

        final byte[] serializedObject = PayloadSerializer.serialize(returnedObject);
        idempotenceAgent.save(hash, serializedObject, ttl);

        idempotenceLogger.logEnd(source);
        return returnedObject;
    }

    private Idempotent extractAnnotation(final ProceedingJoinPoint proceedingJoinPoint) {
        final MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getMethod().getAnnotation(Idempotent.class);
    }

    private String extractSource(final ProceedingJoinPoint proceedingJoinPoint) {
        final MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getMethod().getName();
    }

    private Object[] getValidArguments(final ProceedingJoinPoint proceedingJoinPoint)
            throws ParameterFilterException {
        final MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Idempotent idempotentAnnotation = signature.getMethod().getAnnotation(Idempotent.class);

        final List<String> includes = Arrays.asList(idempotentAnnotation.include());
        final List<String> excludes = Arrays.asList(idempotentAnnotation.exclude());
        final List<String> parameterNames = Arrays.asList(signature.getParameterNames());

        final Object[] allArgs = proceedingJoinPoint.getArgs();
        return ParameterFilter.filter(includes, excludes, parameterNames, allArgs);
    }
}
