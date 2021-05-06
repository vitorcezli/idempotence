package com.example.idempotence.hash;

public interface HashingStrategy {

    String calculateHash(String source, Object[] parameters);
}
