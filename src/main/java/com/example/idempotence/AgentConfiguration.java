package com.example.idempotence;

import com.example.idempotence.idempotent.agents.IdempotenceAgent;
import com.example.idempotence.idempotent.agents.memory.MemoryAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    public IdempotenceAgent generateAgent() {
        return new MemoryAgent();
    }
}
