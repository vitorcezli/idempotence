package vitorcezli.springext.idempotence.hash.implementations;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashCodeStrategyTest {

    private final HashCodeStrategy hashCodeStrategy = new HashCodeStrategy();
    private static final String SOURCE = "source";

    @Test
    void shouldCalculateHashCorrectly1() {
        final MockClass mock1 = new MockClass("string", -1);
        final MockClass mock2 = new MockClass("string", 10);
        final Object[] objects = Arrays.asList(mock1, mock2).toArray();

        final String hash = hashCodeStrategy.calculateHash(SOURCE, objects);
        assertEquals(SOURCE + "--1-10", hash);
    }

    @Test
    void shouldCalculateHashCorrectly2() {
        final MockClass mock1 = new MockClass("string", -1);
        final MockClass mock2 = new MockClass("string", -10);
        final Object[] objects = Arrays.asList(mock1, mock2).toArray();

        final String hash = hashCodeStrategy.calculateHash(SOURCE, objects);
        assertEquals(SOURCE + "--1--10", hash);
    }
}