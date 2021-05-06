package vitorcezli.springext.idempotence.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Configuration
@ConfigurationProperties(prefix = "vitorcezli.spring-ext.idempotence")
@Validated
public class IdempotenceProps {

    @Min(value = 0, message = "must be non-negative")
    private int ttl = 0;

    @NotEmpty
    private String hash = "hashCode";

    private boolean logging = false;

    public void setTtl(final int ttl) {
        this.ttl = ttl;
    }

    public int getTtl() {
        return this.ttl;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public void setLogging(final boolean logging) {
        this.logging = logging;
    }

    public boolean getLogging() {
        return this.logging;
    }
}
