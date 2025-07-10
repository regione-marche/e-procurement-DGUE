package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Getter
public enum Agency {

    EU_COM_GROW("EU-COM-GROW", "DG GROW (European Commission)"),
    EU_COM_OP("EU-COM-OP", "Publications Office of the EU"),
    ISO("ISO", "ISO");

    private final String identifier;

    private final String longName;

    Agency(String identifier, String longName) {
        this.identifier = identifier;
        this.longName = longName;
    }
}
