package com.example.idempotence.idempotent.hash.implementations;

import com.example.idempotence.idempotent.hash.HashingStrategy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HashCodeStrategy implements HashingStrategy {

    @Override
    public String calculateHash(String source, Object[] parameters) {
        final String parametersConcat = Arrays.stream(parameters)
                                              .map(Object::hashCode)
                                              .map(String::valueOf)
                                              .collect(Collectors.joining("-"));
        return source + "-" + parametersConcat;
    }
}
