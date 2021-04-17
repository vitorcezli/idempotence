package com.example.idempotence.idempotent.agents.redis;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class RedisAgent implements IdempotentAgent {

    private final Jedis jedis;

    public RedisAgent(final Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean executed(String hash) {
        return this.jedis.exists(hash.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void save(String hash, byte[] payload, int expireSeconds) {
        if (expireSeconds == 0) {
            this.jedis.set(hash.getBytes(StandardCharsets.UTF_8), payload);
        } else {
            this.jedis.setex(hash.getBytes(StandardCharsets.UTF_8), expireSeconds, payload);
        }
    }
}
