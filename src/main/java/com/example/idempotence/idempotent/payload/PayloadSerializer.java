package com.example.idempotence.idempotent.payload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PayloadSerializer {

    public static byte[] serialize(Serializable serializable) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(serializable);
        objectOutputStream.flush();
        objectOutputStream.close();

        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Serializable deserialize(byte[] binaryObject)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryObject);
        ObjectInput objectInputStream = new ObjectInputStream(byteArrayInputStream);

        Serializable serializable = (Serializable) objectInputStream.readObject();
        objectInputStream.close();

        return serializable;
    }
}
