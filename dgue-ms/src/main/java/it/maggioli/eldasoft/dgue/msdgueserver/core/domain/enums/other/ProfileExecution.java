package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import com.fasterxml.jackson.annotation.JsonValue;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Getter
public enum ProfileExecution implements CodeList {

    ESPD_2_0_0_REGULATED("ESPD-EDMv2.0.0-REGULATED", "ESPD 2.0.0 REGULATED"),
    ESPD_2_0_0_SELFCONTAINED("ESPD-EDMv2.0.0-SELFCONTAINED", "ESPD 2.0.0 SELF-CONTAINED"),
    ESPD_2_1_0_REGULATED("ESPD-EDMv2.1.0-REGULATED", "ESPD 2.1.0 REGULATED"),
    ESPD_2_1_0_SELFCONTAINED("ESPD-EDMv2.1.0-SELFCONTAINED", "ESPD 2.1.0 SELF-CONTAINED"),
    ESPD_1_0_2("ESPD-EDMv1.0.2", "ESPD 1.0.2"),
	ESPD_2_1_1_EXTENDED("ESPD-EDMv2.1.1-EXTENDED","ESPD 2.1.1 EXTENDED");

    private final String code;
    private final String name;

    ProfileExecution(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getListVersionId() {
        return "2.1.0";
    }

    @Override
    public String getListId() {
        return "ProfileExecutionID";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
