package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import com.fasterxml.jackson.annotation.JsonValue;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Getter
public enum ProjectType implements CodeList {

    WORKS("WORKS", "Works", "Lavori"),
    SUPPLIES("SUPPLIES", "Supplies", "Forniture"),
    COMBINED("COMBINED", "Combined", "Appalti misti"),
    SERVICES("SERVICES", "Services", "Servizi"),
    OTHER("OTHER", "Others", "Altro"),
    NOT_APPLICABLE("NOT_APPLICABLE", "Not applicable", "Non applicabile"),
    NOT_SPECIFIED("NOT_SPECIFIED", "Not specified", "Non specificato"),
    CON_PUBLIC_WORKS("CON_PUBLIC_WORKS", "Public Works Concession", "Concessione di lavori pubblici"),
    CON_SERVICE("CON_SERVICE", "Service Concession", "Concessione di servizi");

    private final String code;
    private final String name;
    private final String translation;

    ProjectType(String code, String name, String translation) {
        this.code = code;
        this.name = name;
        this.translation = translation;
    }

    @Override
    public String getListVersionId() {
        return "1.0";
    }

    @Override
    public String getListId() {
        return "ProjectType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_OP.getIdentifier();
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }
}
