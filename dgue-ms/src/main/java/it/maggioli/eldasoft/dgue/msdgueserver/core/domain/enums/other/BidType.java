package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
@Getter
public enum BidType implements CodeList {
    LOT_ALL("LOT_ALL", "Submission for all lots"),
    LOT_ONE("LOT_ONE", "Submission for one lot only"),
    LOT_ONE_MORE("LOT_ONE_MORE", "Submission for one or more lots"),
    OTHER("OTHER", "Others"),
    NOT_APPLICABLE("NOT_APPLICABLE", "Not applicable"),
    NOT_SPECIFIED("NOT_SPECIFIED", "Not specified")
    ;

    private final String code;
    private final String name;

    BidType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListVersionId() {
        return "1.0";
    }

    @Override
    public String getListId() {
        return "BidType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_OP.getIdentifier();
    }
}
