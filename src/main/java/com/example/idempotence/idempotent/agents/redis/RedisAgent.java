package com.example.idempotence.idempotent.agents.redis;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class RedisAgent implements IdempotentAgent {

    private final Jedis jedis;

    public RedisAgent(final String server, final int port) {
        this.jedis = new Jedis(server, port);
    }

    public RedisAgent(final String server, final int port, final String password) {
        this.jedis = new Jedis(server, port);
        this.jedis.auth(password);
    }

    public RedisAgent(final String server, final int port, final String user, final String password) {
        this.jedis = new Jedis(server, port);
        this.jedis.auth(user, password);
    }

    @Override
    public byte[] read(final String hash) {
        final byte[] hashAsBytes = stringToBytes(hash);

        if (!this.jedis.exists(hashAsBytes)) {
            return null;
        }
        return this.jedis.get(hashAsBytes);
    }

    @Override
    public void save(final String hash, final byte[] payload, final int ttl) {
        final byte[] hashAsBytes = stringToBytes(hash);

        if (ttl == 0) {
            this.jedis.set(hashAsBytes, payload);
        } else {
            this.jedis.setex(hashAsBytes, ttl, payload);
        }
    }

    private byte[] stringToBytes(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }
}
