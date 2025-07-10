package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessCriterion extends ExclusionCriterion {

    private String reason;
    private String description;
    private Boolean priorArrangement;
    private Boolean authorizedByJudge;
    private Boolean participationSubordinated;
    private String specify;

    public static BusinessCriterion buildWithExists(boolean exists) {
        BusinessCriterion criterion = new BusinessCriterion();
        criterion.setExists(exists);
        return criterion;
    }
}
