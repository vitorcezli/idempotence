package com.example.idempotence.idempotent.agents;

public interface IdempotentAgent {

    /**
     * @param hash corresponding hash value of the object that will be saved on agent.
     * @return null if hash value has no correspondence; otherwise, return the corresponding object.
     */
    byte[] read(String hash);

    void save(String hash, byte[] payload, int expireSeconds);
}
