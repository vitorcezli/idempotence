package io.github.vitorcezli.idempotence.aspect;

import io.github.vitorcezli.idempotence.configuration.IdempotenceProps;
import io.github.vitorcezli.idempotence.hash.HashingStrategy;
import io.github.vitorcezli.idempotence.hash.HashingStrategyException;
import io.github.vitorcezli.idempotence.hash.HashingStrategySelector;
import io.github.vitorcezli.idempotence.annotations.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PropSelector {

    private final IdempotenceProps idempotenceProps;

    @Autowired
    public PropSelector(final IdempotenceProps idempotenceProps) {
        this.idempotenceProps = idempotenceProps;
    }

    public boolean getLogging() {
        return idempotenceProps.getLogging();
    }

    public HashingStrategy getHashingStrategy(final Idempotent idempotent)
            throws HashingStrategyException {
        String strategyString = idempotent.strategy();
        if (strategyString.length() == 0) {
            strategyString = idempotenceProps.getHash();
        }

        return HashingStrategySelector.select(strategyString);
    }

    public int getTtl(final Idempotent idempotent) {
        final int ttl = idempotent.ttl();
        if (ttl >= 0) {
            return ttl;
        }
        return idempotenceProps.getTtl();
    }
}
