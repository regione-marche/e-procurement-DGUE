package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.EvaluationMethodType;

/**
 * Specific condition that the Economic Operator has to fulfill in order to be considered
 * as a candidate in a procurement process.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CacCriterion extends Serializable {

    String getUuid();

    CbcCriterionTypeCode getCriterionType();

    String getName();

    String getDescription();

    BigDecimal getWeight();

    EvaluationMethodType getEvaluationMethod();

    String getEvaluationMethodDescription();

    List<? extends CacCriterion> getSubTenderingCriterion();

    CacLegislation getLegislation();

    List<? extends CacRequirementGroup> getGroups();

    /**
     * The name of the field in the ESPD model to which the criterion maps.
     *
     * @return
     */
    String getEspdDocumentField();
}
