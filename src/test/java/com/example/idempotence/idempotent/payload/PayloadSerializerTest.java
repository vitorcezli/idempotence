package com.example.idempotence.idempotent.payload;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayloadSerializerTest {

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        final String stringValue = "string";
        final boolean booleanValue = true;
        final int integerValue = 11;
        final MockObject mockObject = new MockObject(stringValue, booleanValue, integerValue);

        final byte[] serializedObject = PayloadSerializer.serialize(mockObject);
        final MockObject returnedObject = (MockObject) PayloadSerializer.deserialize(serializedObject);

        assertEquals(returnedObject.getStringValue(), stringValue);
        assertEquals(returnedObject.getBooleanValue(), booleanValue);
        assertEquals(returnedObject.getIntegerValue(), integerValue);
    }
}