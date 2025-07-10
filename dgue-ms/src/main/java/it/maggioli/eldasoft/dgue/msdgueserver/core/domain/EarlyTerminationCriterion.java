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
public class EarlyTerminationCriterion extends ExclusionCriterion {

	private String description;
	
	
    public static EarlyTerminationCriterion buildWithExists(Boolean exists) {
        EarlyTerminationCriterion earlyTerminationCriterion = new EarlyTerminationCriterion();
        earlyTerminationCriterion.setExists(exists);
        return earlyTerminationCriterion;
    }
}
