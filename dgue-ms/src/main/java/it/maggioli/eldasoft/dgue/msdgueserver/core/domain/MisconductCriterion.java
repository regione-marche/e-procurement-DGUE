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
public class MisconductCriterion extends ExclusionCriterion {

	private String description;
	private Boolean selfCleaningAnswer;
	private String selfCleaningDescription;
	
    public static MisconductCriterion buildWithExists(boolean exists) {
        MisconductCriterion criterion = new MisconductCriterion();
        criterion.setExists(exists);
        return criterion;
    }
}
