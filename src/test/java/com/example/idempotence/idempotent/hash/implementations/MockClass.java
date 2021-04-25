package com.example.idempotence.idempotent.hash.implementations;

class MockClass {

    private final String valueString;

    private final int valueNumber;

    public MockClass(final String valueString, final int valueNumber) {
        this.valueString = valueString;
        this.valueNumber = valueNumber;
    }

    @Override
    public int hashCode() {
        return valueNumber;
    }

    @Override
    public String toString() {
        return valueString;
    }
}
