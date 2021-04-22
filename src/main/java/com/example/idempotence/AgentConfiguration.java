package com.example.idempotence;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.agents.redis.RedisAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    public IdempotentAgent generateAgent() {
        return new RedisAgent("localhost", 6379);
    }
}
