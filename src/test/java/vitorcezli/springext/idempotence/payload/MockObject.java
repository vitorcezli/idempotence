package vitorcezli.springext.idempotence.payload;

import java.io.Serializable;

class MockObject implements Serializable {

    private final String stringValue;
    private final boolean booleanValue;
    private final Integer integerValue;

    public MockObject(final String stringValue, final boolean booleanValue, final Integer integerValue) {
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
        this.integerValue = integerValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public boolean getBooleanValue() {
        return this.booleanValue;
    }

    public Integer getIntegerValue() {
        return this.integerValue;
    }
}
