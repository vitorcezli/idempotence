package com.example.idempotence.idempotent.hash;

public interface HashingStrategy {

    String calculateHash(Object[] objects);
}
