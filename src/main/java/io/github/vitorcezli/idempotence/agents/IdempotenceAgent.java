package io.github.vitorcezli.idempotence.agents;

public interface IdempotenceAgent {

    /**
     * @param hash corresponding hash value of the object that will be saved on agent.
     * @return null if hash value has no correspondence; otherwise, return the corresponding object.
     */
    byte[] read(final String hash);

    void save(final String hash, final byte[] payload, final int ttl);
}
