package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcCriterionTypeCode;
import lombok.Getter;

/**
 * Created by ratoico on 1/15/16 at 3:11 PM.
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OtherCriterionTypeCode implements CbcCriterionTypeCode {

    DATA_ON_ECONOMIC_OPERATOR("Date on economic operator"),
    REDUCTION_OF_CANDIDATES("Reduction of the number of qualified candidates"),
	LOTS_TENDERED(""),
	LOTS_SUBMISSION("");
    private final String description;

    OtherCriterionTypeCode(final String description) {
        this.description = description;
    }

    @Override
    public String getEspdType() {
        return name();
    }

    @Override
    public String getCode() {
        return null;
    }

}
