package com.example.idempotence;

import com.example.idempotence.idempotent.agents.IdempotentAgent;
import com.example.idempotence.idempotent.agents.memory.MemoryAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    public IdempotentAgent generateAgent() {
        return new MemoryAgent();
    }
}
