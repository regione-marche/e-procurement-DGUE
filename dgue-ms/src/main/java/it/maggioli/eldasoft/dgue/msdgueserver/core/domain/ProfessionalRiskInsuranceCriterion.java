package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.math.BigDecimal;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProfessionalRiskInsuranceCriterion extends SelectionCriterion implements UnboundedRequirementGroup {

	private List<DynamicRequirementGroup> unboundedGroups; 
	
	private BigDecimal amount;
	private String amountCurrency;
	private Boolean obtainMinAmount;
	private Boolean exempt;
	
    public static ProfessionalRiskInsuranceCriterion buildWithExists(boolean exists) {
        ProfessionalRiskInsuranceCriterion criterion = new ProfessionalRiskInsuranceCriterion();
        criterion.setExists(exists);
        return criterion;
    }

    @Override
   	public List<DynamicRequirementGroup> getUnboundedGroups() {		
   		return unboundedGroups;
   	}
    
    @Override
	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		// TODO Auto-generated method stub
		return null;
	}
}
