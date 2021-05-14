package vitorcezli.springext.idempotence.hash.implementations;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        assertEquals(SOURCE + "-----string-------otherString", hash);
    }

    @Test
    void shouldCalculateHashCorrectly3() {
        final MockClass mock1 = new MockClass("string", 0);
        final MockClass mock2 = new MockClass("otherString", 0);

        final Object[] objects1 = Arrays.asList(mock1, mock2).toArray();
        final String hash1 = toStringStrategy.calculateHash(SOURCE, objects1);

        final MockClass mock3 = new MockClass("string-otherString", 0);

        final Object[] objects2 = Collections.singletonList(mock3).toArray();
        final String hash2 = toStringStrategy.calculateHash(SOURCE, objects2);

        assertNotEquals(hash1, hash2);
    }
}