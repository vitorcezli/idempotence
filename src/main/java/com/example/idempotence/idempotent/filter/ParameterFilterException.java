package com.example.idempotence.idempotent.filter;

public class ParameterFilterException extends Exception {

    public ParameterFilterException(final String message) {
        super("Invalid filter definition: " + message);
    }
}
