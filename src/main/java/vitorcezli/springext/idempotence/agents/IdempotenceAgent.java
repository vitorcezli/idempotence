package vitorcezli.springext.idempotence.agents;

public interface IdempotenceAgent {

    /**
     * @param hash corresponding hash value of the object that will be saved on agent.
     * @return null if hash value has no correspondence; otherwise, return the corresponding object.
     */
    byte[] read(String hash);

    void save(String hash, byte[] payload, int ttl);
}
