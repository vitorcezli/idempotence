package com.example.idempotence.idempotent.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "vitorcezli.spring-ext.idempotence")
@Validated
public class IdempotenceProps {

    @Min(value = 0, message = "must be between 5 and 25")
    private int ttl;
}
