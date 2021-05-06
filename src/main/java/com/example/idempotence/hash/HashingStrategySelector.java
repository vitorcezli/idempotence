package com.example.idempotence.hash;

import com.example.idempotence.hash.implementations.HashCodeStrategy;
import com.example.idempotence.hash.implementations.ToStringStrategy;

import java.util.HashMap;
import java.util.Map;

public class HashingStrategySelector {

    private final static Map<String, HashingStrategy> strategies = new HashMap<String, HashingStrategy>() {{
        put("toString", new ToStringStrategy());
        put("hashCode", new HashCodeStrategy());
    }};

    public static HashingStrategy select(final String hash) throws HashingStrategyException {
        if (!strategies.containsKey(hash)) {
            throw new HashingStrategyException(hash);
        }
        return strategies.get(hash);
    }
}
