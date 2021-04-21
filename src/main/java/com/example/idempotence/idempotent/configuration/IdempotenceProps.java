package com.example.idempotence.idempotent.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Configuration
@ConfigurationProperties(prefix = "vitorcezli.spring-ext.idempotence")
@Validated
public class IdempotenceProps {

    @Min(value = 0, message = "must be non-negative")
    private int ttl = 0;

    @NotEmpty
    private String hash = "hashCode";

    public void setTtl(final int ttl) {
        this.ttl = ttl;
    }

    public int getTtl() {
        return ttl;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
