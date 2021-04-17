package com.example.idempotence.idempotent.agents.redis;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class RedisAgent implements IdempotentAgent {

    private final Jedis jedis;

    public RedisAgent(final String server, final int port) {
        this.jedis = new Jedis(server, port);
    }

    @Override
    public byte[] read(final String hash) {
        final byte[] hashAsBytes = stringToBytes(hash);
        return this.jedis.get(hashAsBytes);
    }

    @Override
    public boolean executed(final String hash) {
        final byte[] hashAsBytes = stringToBytes(hash);
        return this.jedis.exists(hashAsBytes);
    }

    @Override
    public void save(final String hash, final byte[] payload, final int expireSeconds) {
        final byte[] hashAsBytes = stringToBytes(hash);

        if (expireSeconds == 0) {
            this.jedis.set(hashAsBytes, payload);
        } else {
            this.jedis.setex(hashAsBytes, expireSeconds, payload);
        }
    }

    private byte[] stringToBytes(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }
}
