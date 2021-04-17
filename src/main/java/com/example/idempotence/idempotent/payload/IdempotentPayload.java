package com.example.idempotence.idempotent.payload;

import java.io.Serializable;

public class IdempotentPayload implements Serializable {

    private final boolean isVoid;

    private final Serializable returnObject;

    public IdempotentPayload(final boolean isVoid, final Serializable returnObject) {
        this.isVoid = isVoid;
        this.returnObject = returnObject;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public Serializable getReturnObject() {
        return returnObject;
    }
}
