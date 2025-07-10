package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
@Getter
public enum PropertyGroupType implements CodeList {
    ON_ALL("ON*", "Process the group always"),
    ON_TRUE("ONTRUE", "Process the group if the previous question was and INDICATOR answered with the value 'true'"),
    ON_FALSE("ONFALSE", "Process the group if the previous question was and INDICATOR answered with the value 'false'")
    ;

    private final String code;
    private final String name;

    PropertyGroupType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListVersionId() {
        return "2.1.0";
    }

    @Override
    public String getListId() {
        return "PropertyGroupType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
