package com.example.idempotence.idempotent.hash.concatenator;

import com.example.idempotence.idempotent.hash.HashingStrategy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ConcatenatorHash implements HashingStrategy {

    @Override
    public String calculateHash(Object[] objects) {
        return Arrays.stream(objects)
                     .map(Object::hashCode)
                     .map(String::valueOf)
                     .collect(Collectors.joining("-"));
    }
}
