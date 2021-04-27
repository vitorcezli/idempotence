package com.example.idempotence.idempotent.agents.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        final byte[] bytes = {0x10, 0x11};
        jedis.set(KEY.getBytes(StandardCharsets.UTF_8), bytes);

        final byte[] returnBytes = redisAgent.read(KEY);

        assertArrayEquals(bytes, returnBytes);
    }

    @Test
    void save() {
    }
}