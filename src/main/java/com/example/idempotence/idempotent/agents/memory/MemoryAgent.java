package com.example.idempotence.idempotent.agents.memory;

import com.example.idempotence.idempotent.agents.IdempotentAgent;

import java.util.HashMap;

public class MemoryAgent implements IdempotentAgent {

    private final HashMap<String, byte[]> mapping;

    public MemoryAgent() {
        this.mapping = new HashMap<>();
    }

    @Override
    public byte[] read(final String hash) {
        if (!this.mapping.containsKey(hash)) {
            return null;
        }
        return this.mapping.get(hash);
    }

    @Override
    public void save(final String hash, final byte[] payload, final int expireSeconds) {
        this.mapping.put(hash, payload);
    }
}
