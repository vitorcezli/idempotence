package com.example.idempotence.idempotent.hash;

public class HashingStrategyException extends RuntimeException {

    public HashingStrategyException(final String hashStrategy) {
        super("Invalid hashing strategy: " + hashStrategy);
    }
}
