package com.example.idempotence.idempotent.hash;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToStringStrategy implements HashingStrategy {

    @Override
    public String calculateHash(Object[] objects) {
        return Arrays.stream(objects)
                     .map(Object::toString)
                     .collect(Collectors.joining("-"));
    }
}
