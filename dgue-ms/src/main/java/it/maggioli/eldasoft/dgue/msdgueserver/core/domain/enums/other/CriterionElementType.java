package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
@Getter
public enum CriterionElementType implements CodeList {
    REQUIREMENT("REQUIREMENT", "Requirement, remark, rule, restriction or additional information to which the EO needs to conform or comply with"),
    QUESTION("QUESTION", "A question that requires an answer (a specific datum) from the EO"),
    CAPTION("CAPTION", "A text label (no requirement nor answer is expected)")
    ;

    private final String code;
    private final String name;

    CriterionElementType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListVersionId() {
        return "2.1.1";
    }

    @Override
    public String getListId() {
        return "CriterionElementType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
