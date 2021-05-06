package com.example.idempotence.hash.implementations;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToStringStrategyTest {

    private final ToStringStrategy toStringStrategy = new ToStringStrategy();
    private static final String SOURCE = "source";

    @Test
    void shouldCalculateHashCorrectly1() {
        final MockClass mock1 = new MockClass("string1", 0);
        final MockClass mock2 = new MockClass("string2", 0);
        final Object[] objects = Arrays.asList(mock1, mock2).toArray();

        final String hash = toStringStrategy.calculateHash(SOURCE, objects);
        assertEquals(SOURCE + "-string1-string2", hash);
    }

    @Test
    void shouldCalculateHashCorrectly2() {
        final MockClass mock1 = new MockClass("--string", 0);
        final MockClass mock2 = new MockClass("---otherString", 0);
        final Object[] objects = Arrays.asList(mock1, mock2).toArray();

        final String hash = toStringStrategy.calculateHash(SOURCE, objects);
        assertEquals(SOURCE + "---string----otherString", hash);
    }
}