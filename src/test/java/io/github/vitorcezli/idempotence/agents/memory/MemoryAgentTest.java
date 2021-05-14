package io.github.vitorcezli.idempotence.agents.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MemoryAgentTest {

    private static final String KEY = "key";
    private static final String INVALID_KEY = "invalid_key";

    private final MemoryAgent memoryAgent = new MemoryAgent();

    @Test
    void shouldReturnNullForWhenReadingInvalidKey() {
        assertNull(memoryAgent.read(INVALID_KEY));
    }

    @Test
    void shouldSaveCorrectBytesWithoutTtl() {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 0);

        final byte[] returnBytes = memoryAgent.read(KEY);
        assertArrayEquals(payload, returnBytes);
    }

    @Test
    void shouldSaveCorrectBytesWithTtl() {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 10);

        final byte[] returnBytes = memoryAgent.read(KEY);
        assertArrayEquals(payload, returnBytes);
    }

    @Test
    void shouldDeleteAfterTtl1() throws InterruptedException {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 1);

        Thread.sleep(1200);

        assertNull(memoryAgent.read(KEY));
    }

    @Test
    void shouldNotDeleteBeforeTtl1() throws InterruptedException {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 1);

        Thread.sleep(800);

        assertNotNull(memoryAgent.read(KEY));
    }

    @Test
    void shouldDeleteAfterTtl2() throws InterruptedException {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 2);

        Thread.sleep(2200);

        assertNull(memoryAgent.read(KEY));
    }

    @Test
    void shouldNotDeleteBeforeTtl2() throws InterruptedException {
        final byte[] payload = generatePayload();
        memoryAgent.save(KEY, payload, 2);

        Thread.sleep(1800);

        assertNotNull(memoryAgent.read(KEY));
    }

    private byte[] generatePayload() {
        return new byte[] {0x10, 0x11};
    }
}