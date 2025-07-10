package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcCriterionTypeCode;
import lombok.Getter;

/**
 * Created by vigi on 11/16/15:4:59 PM.
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExclusionCriterionTypeCode implements CbcCriterionTypeCode {

    /**
     * Grounds for exclusion for bankruptcy of insolvency
     */
    BANKRUPTCY_INSOLVENCY("Grounds for exclusion for bankruptcy or insolvency"),
    /**
     * Grounds for exclusion relating to possible conflicts of interests
     */
    CONFLICT_OF_INTEREST("Grounds for exclusion relating to possible conflicts of interests"),
    /**
     * Grounds for exclusion relating to criminal convictions
     */
    CRIMINAL_CONVICTIONS("Grounds for exclusion relating to criminal convictions"),
    /**
     * Grounds for exclusion for misconduct
     */
    DISTORTING_MARKET("Grounds for exclusion related to market distortion"),
    /**
     * Grounds for exclusion relating to the breaching of environmental laws
     */
    ENVIRONMENTAL_LAW("Grounds for exclusion relating to the breaching of environmental laws"),
    /**
     * Grounds for exclusion relating to the breaching of labour laws
     */
    LABOUR_LAW("Grounds for exclusion relating to the breaching of labour laws"),
    /**
     * Grounds for exclusion for misconduct
     */
    MISCONDUCT("Grounds for exclusion for misconduct"),
    /**
     * Grounds for exclusion relating to the payment of social security contributions
     */
    PAYMENT_OF_SOCIAL_SECURITY("Grounds for exclusion relating to the payment of social security contributions"),
    /**
     * Grounds for exclusion relating to the payment of taxes contributions
     */
    PAYMENT_OF_TAXES("Grounds for exclusion relating to the payment of taxes contributions"),
    /**
     * Grounds for exclusion relating to the breaching of social laws
     */
    SOCIAL_LAW("Grounds for exclusion relating to the breaching of social laws"),
    /**
     * Other exclusion grounds that may be foreseen in the national legislation of the contracting authority's
     * or contracting entity's Member State
     */
    OTHER("Other exclusion grounds that may be foreseen in the national legislation of the contracting authority's or contracting entity's Member State"),
	
	EARLY_TERMINATION("EarlyTerminationCriterion"),
	
	MISINTERPRETATION("MisinterpretationCriterion");

    private final String description;

    ExclusionCriterionTypeCode(final String description) {
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
