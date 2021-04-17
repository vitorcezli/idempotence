package com.example.idempotence;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.agents.memory.MemoryAgent;
import com.example.idempotence.idempotent.agents.redis.RedisAgent;
import com.example.idempotence.idempotent.hash.HashingStrategy;
import com.example.idempotence.idempotent.hash.concatenator.ConcatenatorHash;
import com.example.idempotence.idempotent.payload.IdempotentPayload;
import com.example.idempotence.idempotent.payload.IdempotentPayloadSerializer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Aspect
@Component
public class IdempotenceAspect {

    private final Jedis jedis = new Jedis();
    private final IdempotentAgent idempotentAgent = new RedisAgent(jedis);
    // private final IdempotentAgent idempotentAgent = new MemoryAgent();
    private final HashingStrategy hashingStrategy = new ConcatenatorHash();

    @Around("@annotation(com.example.idempotence.idempotent.annotations.Idempotent)")
    public void assertIdempotence(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();

        if (objects.length == 0) {
            System.out.println("This code will always be executed");
            return;
        }

        String hash = hashingStrategy.calculateHash(objects);
        if (idempotentAgent.executed(hash)) {
            System.out.println("Object already executed");
            return;
        }

        byte[] payload = IdempotentPayloadSerializer.serialize(buildIdempotentPayload());
        idempotentAgent.save(hash, payload, 10);

        joinPoint.proceed();
    }

    private IdempotentPayload buildIdempotentPayload() {
        return new IdempotentPayload(true, null);
    }
}
