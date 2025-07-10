package it.maggioli.eldasoft.dgue.msdgueserver.exceptions;

import java.util.ArrayList;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessage;


/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public abstract class DGUEBaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 66375714872697245L;

    private List<DGUEMessage> messages = new ArrayList<>();

    /**
     * @param message
     * @param cause
     */
    protected DGUEBaseRuntimeException(final String className, final DGUEMessage message, final Throwable cause) {
        super(String.format("[%s={code: %s, severity: %s}]", className, message.getCode().name(), message.getSeverity().name()), cause);
        messages.add(message);
    }

    protected DGUEBaseRuntimeException(final String className, final List<DGUEMessage> messages, final Throwable cause) {
        super(String.format("[%s={%s}]", className, messages.get(0).getCode()), cause);
        this.messages = messages;
    }

    /**
     * @return
     */
    public List<DGUEMessage> getMessages() {
        return this.messages;
    }
}
