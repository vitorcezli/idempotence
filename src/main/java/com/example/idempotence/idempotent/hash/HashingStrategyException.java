package com.example.idempotence.idempotent.hash;

public class HashingStrategyException extends Exception {

    public HashingStrategyException(String hashStrategy) {
        super("Invalid hashing strategy: " + hashStrategy);
    }
}
