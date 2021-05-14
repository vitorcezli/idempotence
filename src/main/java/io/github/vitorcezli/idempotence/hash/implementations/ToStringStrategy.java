package io.github.vitorcezli.idempotence.hash.implementations;

import io.github.vitorcezli.idempotence.hash.HashingStrategy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToStringStrategy extends HashingStrategy {

    @Override
    protected String calculateHashObjects(final Object[] objects) {
        return Arrays.stream(objects)
                .map(Object::toString)
                .map(this::stringDuplicatedSeparatorCharacter)
                .collect(Collectors.joining("-"));
    }

    private String stringDuplicatedSeparatorCharacter(final String source) {
        return source.replaceAll("-", "--");
    }
}
