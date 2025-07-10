package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.math.BigDecimal;
import java.util.Date;
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
public class FinancialRatiosCriterion extends SelectionCriterion  implements UnboundedRequirementGroup {

	private List<DynamicRequirementGroup> unboundedGroups; 

	private String ratioType;	
	private String definition;
	private BigDecimal minRequirement;
	private BigDecimal ratio;
	private Date startDate;
	private Date endDate;
	
    public static FinancialRatiosCriterion buildWithExists(boolean exists) {
        FinancialRatiosCriterion criterion = new FinancialRatiosCriterion();
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
