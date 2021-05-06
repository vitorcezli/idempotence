package vitorcezli.springext.idempotence.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdempotenceLogger {

    private final Logger logger = LogManager.getLogger(IdempotenceLogger.class);

    private final boolean loggingEnabled;

    private final String loggingPrefix;

    public IdempotenceLogger(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        this.loggingPrefix = "[IdempotenceLogger]";
    }

    public void logAlwaysExecuted(final String source) {
        this.logWarn(source + " will always be executed");
    }

    public void logStart(final String source) {
        this.logInfo("Started for source " + source);
    }

    public void logExisting(final String source) {
        this.logInfo(source + " won't be executed to assure idempotence");
    }

    public void logNew(final String source) {
        this.logInfo(source + " will be executed");
    }

    public void logExecution(final String source, final int ttl) {
        this.logInfo(source + " executed with ttl of " + ttl + " second(s)");
    }

    public void logEnd(final String source) {
        this.logInfo("Ended for source " + source);
    }

    private void logInfo(final String message) {
        if (loggingEnabled) {
            logger.info("{}: {}", loggingPrefix, message);
        }
    }

    private void logWarn(final String message) {
        if (loggingEnabled) {
            logger.warn("{}: {}", loggingPrefix, message);
        }
    }
}
