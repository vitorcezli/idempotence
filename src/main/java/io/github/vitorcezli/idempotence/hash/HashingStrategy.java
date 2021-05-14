package io.github.vitorcezli.idempotence.hash;

public abstract class HashingStrategy {

    public String calculateHash(final String source, final Object[] objects) {
        return source + "-" + calculateHashObjects(objects);
    }

    protected abstract String calculateHashObjects(final Object[] objects);
}
