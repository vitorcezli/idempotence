package vitorcezli.springext.idempotence.filter;

public class ParameterFilterException extends RuntimeException {

    public ParameterFilterException(final String message) {
        super("Invalid filter definition: " + message);
    }
}
