package io.github.vitorcezli.idempotence.agents.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RedisAgentTest {

    private static final String KEY = "key";
    private static final String INVALID_KEY = "invalid_key";

    private RedisAgent redisAgent;
    private Jedis jedis;

    @Container
    private final GenericContainer container =
            new GenericContainer(DockerImageName.parse("redis:6"))
                    .withExposedPorts(6379);

    @BeforeEach
    public void setUp() {
        redisAgent = new RedisAgent(container.getHost(), container.getFirstMappedPort());
        jedis = new Jedis(container.getHost(), container.getFirstMappedPort());
    }

    @Test
    void shouldReturnNullForWhenReadingInvalidKey() {
        assertNull(redisAgent.read(INVALID_KEY));
    }

    @Test
    void shouldReturnBytesSavedOnKey() {
        final byte[] payload = generatePayload();
        jedis.set(keyAsBytes(), payload);

        final byte[] returnBytes = redisAgent.read(KEY);

        assertArrayEquals(payload, returnBytes);
    }

    @Test
    void shouldSaveWithoutTtl() {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 0);

        // -1 is returned if key doesn't have expiration time.
        assertEquals(jedis.ttl(KEY), -1);
    }

    @Test
    void shouldSaveWithTtl() {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 10);
        assertEquals(jedis.ttl(KEY), 10);
    }

    @Test
    void shouldSaveCorrectBytesWithoutTtl() {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 0);

        final byte[] returnBytes = jedis.get(keyAsBytes());
        assertArrayEquals(payload, returnBytes);
    }

    @Test
    void shouldSaveCorrectBytesWithTtl() {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 10);

        final byte[] returnBytes = jedis.get(keyAsBytes());
        assertArrayEquals(payload, returnBytes);
    }

    @Test
    void shouldDeleteAfterTtl1() throws InterruptedException {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 1);

        Thread.sleep(1200);

        assertFalse(jedis.exists(KEY));
    }

    @Test
    void shouldNotDeleteBeforeTtl1() throws InterruptedException {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 1);

        Thread.sleep(800);

        assertTrue(jedis.exists(KEY));
    }

    @Test
    void shouldDeleteAfterTtl2() throws InterruptedException {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 2);

        Thread.sleep(2200);

        assertFalse(jedis.exists(KEY));
    }

    @Test
    void shouldNotDeleteBeforeTtl2() throws InterruptedException {
        final byte[] payload = generatePayload();
        redisAgent.save(KEY, payload, 2);

        Thread.sleep(1800);

        assertTrue(jedis.exists(KEY));
    }

    private byte[] keyAsBytes() {
        return KEY.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generatePayload() {
        return new byte[] {0x10, 0x11};
    }
}