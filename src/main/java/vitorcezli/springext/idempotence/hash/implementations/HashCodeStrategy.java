package vitorcezli.springext.idempotence.hash.implementations;

import vitorcezli.springext.idempotence.hash.HashingStrategy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HashCodeStrategy extends HashingStrategy {

    @Override
    protected String calculateHashObjects(final Object[] objects) {
        return Arrays.stream(objects)
                .map(Object::hashCode)
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }
}
