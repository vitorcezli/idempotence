package com.example.idempotence.idempotent.hash.implementations;

import com.example.idempotence.idempotent.hash.HashingStrategy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToStringStrategy implements HashingStrategy {

    @Override
    public String calculateHash(String source, Object[] parameters) {
        final String parametersConcat = Arrays.stream(parameters)
                                              .map(Object::toString)
                                              .collect(Collectors.joining("-"));
        return source + "-" + parametersConcat;
    }
}
