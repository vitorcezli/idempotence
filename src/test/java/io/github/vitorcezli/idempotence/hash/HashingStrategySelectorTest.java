package io.github.vitorcezli.idempotence.hash;

import io.github.vitorcezli.idempotence.hash.implementations.HashCodeStrategy;
import io.github.vitorcezli.idempotence.hash.implementations.ToStringStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashingStrategySelectorTest {

    @Test
    void assertReturnsToStringStrategyOnSelect() throws HashingStrategyException {
        final HashingStrategy hashingStrategy = HashingStrategySelector.select("toString");
        assertTrue(hashingStrategy instanceof ToStringStrategy);
    }

    @Test
    void assertReturnsHashCodeStrategyOnSelect() throws HashingStrategyException {
        final HashingStrategy hashingStrategy = HashingStrategySelector.select("hashCode");
        assertTrue(hashingStrategy instanceof HashCodeStrategy);
    }

    @Test
    void assertThrowsHashStrategyExceptionOnSelect() {
        assertThrows(HashingStrategyException.class, () -> HashingStrategySelector.select("invalidHash"));
    }
}