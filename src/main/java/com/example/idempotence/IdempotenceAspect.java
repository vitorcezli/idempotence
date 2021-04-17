package com.example.idempotence;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.agents.redis.RedisAgent;
import com.example.idempotence.idempotent.hash.HashingStrategy;
import com.example.idempotence.idempotent.hash.hashcode.HashCodeStrategy;
import com.example.idempotence.idempotent.payload.IdempotentPayload;
import com.example.idempotence.idempotent.payload.IdempotentPayloadSerializer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

@Aspect
@Component
public class IdempotenceAspect {

    private final Jedis jedis = new Jedis();
    private final IdempotentAgent idempotentAgent = new RedisAgent(jedis);
    // private final IdempotentAgent idempotentAgent = new MemoryAgent();
    private final HashingStrategy hashingStrategy = new HashCodeStrategy();

    @Around("@annotation(com.example.idempotence.idempotent.annotations.Idempotent)")
    public Object assertIdempotence(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();

        if (objects.length == 0) {
            System.out.println("This code will always be executed");
            return joinPoint.proceed();
        }

        String hash = hashingStrategy.calculateHash(objects);
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

    private IdempotentPayload buildIdempotentPayload(final Object returnObject) {
        return new IdempotentPayload(false, (Serializable) returnObject);
    }
}
