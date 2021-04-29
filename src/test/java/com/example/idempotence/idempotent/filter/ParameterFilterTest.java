package com.example.idempotence.idempotent.filter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterFilterTest {

    @Test
    void filterShouldThrowParameterFilterExceptionIfBothIncludesAndExcludesAreDefined() {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        assertThrows(ParameterFilterException.class,
                () -> ParameterFilter.filter(includes, excludes, parameters, objects));
    }

    private List<String> generateParameters() {
        return Arrays.asList("a", "b", "c");
    }

    private Object[] generateObjects() {
        return Arrays.asList("objectA", "objectB", "objectC").toArray();
    }
}