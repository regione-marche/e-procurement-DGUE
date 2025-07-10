package it.maggioli.eldasoft.dgue.msdgueserver.core.message;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 26, 2020
 */
public class DGUEMessage {
    private DGUEMessageCode code;
    private DGUEMessageSeverity severity;
    private String[] params = null;

    protected  DGUEMessage() {
        this(null, null, null);
    }

    DGUEMessage(DGUEMessageCode code, DGUEMessageSeverity severity) {
        this(code, severity, null);
    }

    DGUEMessage(DGUEMessageCode code, DGUEMessageSeverity severity, String[] params) {
        this.code = code;
        this.severity = severity;
        this.params = params;
    }

    public DGUEMessageCode getCode() {
        return code;
    }

    public void setCode(DGUEMessageCode code) {
        this.code = code;
    }

    public DGUEMessageSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DGUEMessageSeverity severity) {
        this.severity = severity;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
    
}
