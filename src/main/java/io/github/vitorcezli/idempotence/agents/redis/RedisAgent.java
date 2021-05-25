package io.github.vitorcezli.idempotence.agents.redis;

import io.github.vitorcezli.idempotence.agents.IdempotenceAgent;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class RedisAgent implements IdempotenceAgent {

    private final Jedis jedis;

    public RedisAgent(final String uri) {
        this.jedis = new Jedis(uri);
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
