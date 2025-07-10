package it.maggioli.eldasoft.dgue.msdgueserver.exceptions;

import java.util.Arrays;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessage;


/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public class BadRequestException extends it.maggioli.eldasoft.dgue.msdgueserver.exceptions.DGUEBaseRuntimeException {

    private static final long serialVersionUID = -6546971195087052127L;

    public BadRequestException(final DGUEMessage message) {
        this(message, null);
    }


    /**
     * @param message
     * @param cause
     */
    public BadRequestException(final DGUEMessage message, final Throwable cause) {
        this(Arrays.asList(message), cause);
    }

    /**
     *
     * @param messages
     * @param cause
     */
    public BadRequestException(final List<DGUEMessage> messages, final Throwable cause) {
        super("BadRequestException", messages, cause);
    }
}
