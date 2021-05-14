package io.github.vitorcezli.idempotence.payload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PayloadSerializer {

    public static byte[] serialize(final Object object) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();

        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(final byte[] binaryObject) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryObject);
        final ObjectInput objectInputStream = new ObjectInputStream(byteArrayInputStream);

        final Object returnedObject = objectInputStream.readObject();
        objectInputStream.close();

        return returnedObject;
    }
}
