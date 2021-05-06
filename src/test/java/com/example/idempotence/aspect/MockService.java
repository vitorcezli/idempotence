package com.example.idempotence.aspect;

import com.example.idempotence.annotations.Idempotent;
import org.springframework.stereotype.Service;

@Service
class MockService {

    private boolean executed;

    public void resetExecution() {
        this.executed = false;
    }

    public boolean getExecuted() {
        return executed;
    }

    @Idempotent(strategy = "hashCode")
    public String getSomething(final String returnValue) {
        executed = true;
        return returnValue;
    }

    @Idempotent(exclude = "invalid")
    public String invalidExcludes(final String returnValue) {
        executed = true;
        return returnValue;
    }

    @Idempotent(strategy = "invalid")
    public String invalidHashing(final String returnValue) {
        executed = true;
        return returnValue;
    }

    @Idempotent(include = "returnValue", exclude = "returnValue")
    public String includeAndExclude(final String returnValue) {
        executed = true;
        return returnValue;
    }

    @Idempotent(include = "value1")
    public String include(final String value1, final String value2) {
        executed = true;
        return value1 + value2;
    }

    @Idempotent(exclude = "value1")
    public String exclude(final String value1, final String value2) {
        executed = true;
        return value1 + value2;
    }
}
