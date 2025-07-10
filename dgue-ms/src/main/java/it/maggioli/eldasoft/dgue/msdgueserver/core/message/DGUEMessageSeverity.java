package it.maggioli.eldasoft.dgue.msdgueserver.core.message;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public enum DGUEMessageSeverity {
    FATAL, ERROR, WARN, INFO;

    public static DGUEMessageSeverity getSeverity(final String severity) {
        if (severity == null) {
            return null;
        }
        final String severityLowerCase = severity.toLowerCase();
        switch (severityLowerCase) {
            case "fatal":
                return FATAL;
            case "error":
                return ERROR;
            case "warn":
            case "warning":
                return WARN;
            case "info":
            case "information":
                return INFO;
            default:
                return null;
        }
    }
}
