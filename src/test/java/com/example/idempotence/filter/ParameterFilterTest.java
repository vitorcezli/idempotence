package com.example.idempotence.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterFilterTest {

    @Test
    @DisplayName("Filter should throw ParameterFilterException if includes and excludes are both defined")
    void testExceptionWhenBothAreDefined() {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        assertThrows(ParameterFilterException.class,
                () -> ParameterFilter.filter(includes, excludes, parameters, objects));
    }

    @Test
    @DisplayName("Filter should throw ParameterFilterException if some inclusion argument is invalid")
    void testExceptionWhenIncludeIsInvalid() {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        includes.set(0, "invalid");
        assertThrows(ParameterFilterException.class,
                () -> ParameterFilter.filter(includes, excludes, parameters, objects));
    }

    @Test
    @DisplayName("Filter should throw ParameterFilterException if some exclusion argument is invalid")
    void testExceptionWhenExcludeIsInvalid() {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        excludes.set(0, "invalid");
        assertThrows(ParameterFilterException.class,
                () -> ParameterFilter.filter(includes, excludes, parameters, objects));
    }

    @Test
    @DisplayName("Filter should include all parameters")
    void testInclusionAllParameters() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, excludes, parameters, objects);
        assertArrayEquals(objects, returnedObj);
    }

    @Test
    @DisplayName("Filter should include the first parameter")
    void testInclusionFirstParameter() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(Collections.singletonList(includes.get(0)),
                excludes, parameters, objects);
        assertArrayEquals(Collections.singletonList(objects[0]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should include the second parameter")
    void testInclusionSecondParameter() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(Collections.singletonList(includes.get(1)),
                excludes, parameters, objects);
        assertArrayEquals(Collections.singletonList(objects[1]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should include the last parameter")
    void testInclusionLastParameter() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(Collections.singletonList(includes.get(2)),
                excludes, parameters, objects);
        assertArrayEquals(Collections.singletonList(objects[2]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should execute correctly for duplicated inclusion")
    void testDuplicatedInclusion() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(Arrays.asList(includes.get(1), includes.get(1)),
                excludes, parameters, objects);
        assertArrayEquals(Collections.singletonList(objects[1]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should include the first and the last parameter")
    void testInclusionWithMoreParameters() throws ParameterFilterException {
        final List<String> includes = generateParameters();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(Arrays.asList(includes.get(0), includes.get(2)),
                excludes, parameters, objects);
        assertArrayEquals(Arrays.asList(objects[0], objects[2]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should exclude all parameters")
    void testExclusionAllParameters() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, excludes, parameters, objects);
        assertEquals(returnedObj.length, 0);
    }

    @Test
    @DisplayName("Filter should exclude the first parameter")
    void testExclusionFirstParameters() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, Collections.singletonList(excludes.get(0)),
                parameters, objects);
        assertArrayEquals(Arrays.asList(objects[1], objects[2]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should exclude the second parameter")
    void testExclusionSecondParameters() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, Collections.singletonList(excludes.get(1)),
                parameters, objects);
        assertArrayEquals(Arrays.asList(objects[0], objects[2]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should exclude the last parameter")
    void testExclusionLastParameters() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, Collections.singletonList(excludes.get(2)),
                parameters, objects);
        assertArrayEquals(Arrays.asList(objects[0], objects[1]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should execute correctly for duplicated exclusion")
    void testDuplicatedExclusion() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes,
                Arrays.asList(excludes.get(2), excludes.get(2)), parameters, objects);
        assertArrayEquals(Arrays.asList(objects[0], objects[1]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should exclude the first and the second parameter")
    void testExclusionWithMoreParameters() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateParameters();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes,
                Arrays.asList(excludes.get(0), excludes.get(1)), parameters, objects);
        assertArrayEquals(Collections.singletonList(objects[2]).toArray(), returnedObj);
    }

    @Test
    @DisplayName("Filter should return the original objects")
    void testEmptyInclusionAndExclusion() throws ParameterFilterException {
        final List<String> includes = generateEmptyList();
        final List<String> excludes = generateEmptyList();
        final List<String> parameters = generateParameters();
        final Object[] objects = generateObjects();

        final Object[] returnedObj = ParameterFilter.filter(includes, excludes, parameters, objects);
        assertArrayEquals(objects, returnedObj);
    }

    private List<String> generateParameters() {
        return Arrays.asList("a", "b", "c");
    }

    private List<String> generateEmptyList() {
        return Collections.singletonList("");
    }

    private Object[] generateObjects() {
        return Arrays.asList("objectA", "objectB", "objectC").toArray();
    }
}