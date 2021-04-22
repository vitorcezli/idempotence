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
        return this.mapping.get(hash);
    }

    @Override
    public boolean executed(final String hash) {
        return this.mapping.containsKey(hash);
    }

    @Override
    public void save(final String hash, final byte[] payload, final int expireSeconds) {
        this.mapping.put(hash, payload);
    }
}
