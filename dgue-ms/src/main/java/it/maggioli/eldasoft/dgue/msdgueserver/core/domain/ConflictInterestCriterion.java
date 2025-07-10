package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConflictInterestCriterion extends ExclusionCriterion {

	private String description;
	
    public static ConflictInterestCriterion buildWithExists(boolean exists) {
        ConflictInterestCriterion criterion = new ConflictInterestCriterion();
        criterion.setExists(exists);
        return criterion;
    }

	
}
