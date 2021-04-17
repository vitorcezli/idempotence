package com.example.idempotence.idempotent.payload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdempotentPayloadSerializer {

    public static byte[] serialize(IdempotentPayload idempotentPayload) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(idempotentPayload);
        objectOutputStream.flush();
        objectOutputStream.close();

        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static IdempotentPayload deserialize(byte[] binaryObject)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryObject);
        ObjectInput objectInputStream = new ObjectInputStream(byteArrayInputStream);

        IdempotentPayload idempotentPayload = (IdempotentPayload) objectInputStream.readObject();
        objectInputStream.close();

        return idempotentPayload;
    }
}
