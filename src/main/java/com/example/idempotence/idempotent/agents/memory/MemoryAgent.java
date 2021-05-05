package com.example.idempotence.idempotent.agents.memory;

import com.example.idempotence.idempotent.agents.IdempotenceAgent;
import org.apache.commons.collections4.map.PassiveExpiringMap;

import java.util.HashMap;

public class MemoryAgent implements IdempotenceAgent {

    private final HashMap<Integer, PassiveExpiringMap<String, byte[]>> mapping;

    public MemoryAgent() {
        this.mapping = new HashMap<>();
    }

    @Override
    public byte[] read(final String hash) {
        for (final PassiveExpiringMap<String, byte[]> map : this.mapping.values()) {
            byte[] object = map.get(hash);
            if (null != object) {
                return object;
            }
        }
        return null;
    }

    @Override
    public void save(final String hash, final byte[] payload, final int ttl) {
        assurePassiveExpiringMapForTtl(ttl);
        final PassiveExpiringMap<String, byte[]> map = this.mapping.get(ttl);
        map.put(hash, payload);
    }

    private void assurePassiveExpiringMapForTtl(final int ttl) {
        if (!this.mapping.containsKey(ttl)) {
            final PassiveExpiringMap<String, byte[]> map = generateMapForTtl(ttl);
            this.mapping.put(ttl, map);
        }
    }

    private PassiveExpiringMap<String, byte[]> generateMapForTtl(final int ttl) {
        if (ttl == 0) {
            return new PassiveExpiringMap<>();
        }
        final int ttlMillis = ttl * 1000;
        return new PassiveExpiringMap<>(ttlMillis);
    }
}
