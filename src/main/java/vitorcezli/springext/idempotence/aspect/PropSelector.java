package vitorcezli.springext.idempotence.aspect;

import vitorcezli.springext.idempotence.annotations.Idempotent;
import vitorcezli.springext.idempotence.configuration.IdempotenceProps;
import vitorcezli.springext.idempotence.hash.HashingStrategy;
import vitorcezli.springext.idempotence.hash.HashingStrategyException;
import vitorcezli.springext.idempotence.hash.HashingStrategySelector;
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
