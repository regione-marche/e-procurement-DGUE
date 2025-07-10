package it.maggioli.eldasoft.dgue.msdgueserver.exceptions;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public class DGUEMessageException extends RuntimeException {
    private static final long serialVersionUID = -8918595067663723953L;

    private String message;

    public DGUEMessageException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
