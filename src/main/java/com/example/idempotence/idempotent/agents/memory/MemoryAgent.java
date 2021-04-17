package com.example.idempotence.idempotent.agents.memory;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.payload.IdempotentPayload;

import java.util.HashMap;

public class MemoryAgent implements IdempotentAgent {

    private final HashMap<String, byte[]> mapping = new HashMap<>();

    @Override
    public boolean executed(String hash) {
        return this.mapping.containsKey(hash);
    }

    @Override
    public void save(String hash, byte[] payload, int expireSeconds) {
        this.mapping.put(hash, payload);
    }
}
