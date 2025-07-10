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
public class MisinterpretationCriterion extends ExclusionCriterion {

	@Override
    public Boolean getAnswer() {
        if (this.answer == null) {
            // exclusion criteria with no answer have a default value of FALSE
            return Boolean.TRUE;
        }
        return this.answer;
    }
	private String description;
	
    public static MisinterpretationCriterion buildWithExists(Boolean exists) {
        MisinterpretationCriterion criterion = new MisinterpretationCriterion();
        criterion.setExists(exists);
        return criterion;
    }
}
