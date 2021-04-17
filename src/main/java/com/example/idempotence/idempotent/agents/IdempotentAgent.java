package com.example.idempotence.idempotent.agents;

public interface IdempotentAgent {

    boolean executed(String hash);

    byte[] read(String hash);

    void save(String hash, byte[] payload, int expireSeconds);
}
