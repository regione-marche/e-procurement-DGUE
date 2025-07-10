package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
@Getter
public enum EvaluationMethodType implements CodeList {

    PASSFAIL("PASSFAIL", "Pass or Fail"),
    WEIGHTED("WEIGHTED", "The Criterion is weighted")
    ;

    private final String code;
    private final String name;

    EvaluationMethodType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListVersionId() {
        return "2.1.0";
    }

    @Override
    public String getListId() {
        return "EvaluationMethodType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
