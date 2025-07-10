package it.maggioli.eldasoft.dgue.msdgueserver.exceptions;


import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessage;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public class ResourceNotFoundException extends it.maggioli.eldasoft.dgue.msdgueserver.exceptions.DGUEBaseRuntimeException {

    private static final long serialVersionUID = -8232783649217945554L;

    public ResourceNotFoundException(final DGUEMessage message) {
        this(message, null);
    }

    /**
     * @param message
     * @param cause
     */
    public ResourceNotFoundException(final DGUEMessage message, final Throwable cause) {
        super("ResourceNotFoundException", message, cause);
    }

    /**
     *
     * @return
     */
    public DGUEMessage getDGUEMessage() {
        return this.getMessages().get(0);
    }
}
