package it.maggioli.eldasoft.dgue.msdgueserver.core.message;

import it.maggioli.eldasoft.dgue.msdgueserver.exceptions.DGUEMessageException;
import org.springframework.stereotype.Service;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 26, 2020
 */
@Service
public class DGUEMessageBuilder {

    public DGUEMessage buildMessage(DGUEMessageCode code) throws DGUEMessageException {
        return this.buildMessage(code, DGUEMessageSeverity.ERROR, (String[])null);
    }

    public DGUEMessage buildMessage(DGUEMessageCode code, DGUEMessageSeverity severity) throws DGUEMessageException {
        return this.buildMessage(code, severity, (String[])null);
    }

    public DGUEMessage buildMessage(DGUEMessageCode code, String ...params) throws DGUEMessageException {
        return this.buildMessage(code, DGUEMessageSeverity.ERROR, params);
    }

    public DGUEMessage buildMessage(DGUEMessageCode code, DGUEMessageSeverity severity, String ...params) throws DGUEMessageException {
        if (code == null) {
            throw new DGUEMessageException("code is mandatory");
        }
        return new DGUEMessage(code, severity, params);
    }
}
