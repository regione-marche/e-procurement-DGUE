package it.maggioli.eldasoft.dgue.msdgueserver.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.message.DGUEMessage;


/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public class ResponseBean implements Serializable {

    private static final long serialVersionUID = 8026844755541131888L;
    private String done;
    private Object response;
    private List<DGUEMessage> messages = new ArrayList<>();

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public List<DGUEMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<DGUEMessage> messages) {
        this.messages = messages;
    }
}
