package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SetupEconomicOperatorCriterion extends SelectionCriterion {

	private Date year;
	
    public static SetupEconomicOperatorCriterion buildWithExists(boolean exists) {
        SetupEconomicOperatorCriterion criterion = new SetupEconomicOperatorCriterion();
        criterion.setExists(exists);
        return criterion;
    }
    
    
}
