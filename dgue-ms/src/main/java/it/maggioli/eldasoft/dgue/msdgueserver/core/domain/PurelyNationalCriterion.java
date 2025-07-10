package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

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
public class PurelyNationalCriterion extends ExclusionCriterion implements UnboundedRequirementGroup{

	private Boolean answer2;
	private Boolean answer3;
	private Boolean answer4;
	private Boolean answer5;
	private Boolean answer6;
	private Boolean answer7;
	private Boolean answer8;
	private Boolean answer9;
	private Boolean answer10;
	private Boolean answer11;
	private Boolean answer12;
	private List<DynamicRequirementGroup> unboundedGroups; 
	private List<DynamicRequirementGroup> unboundedGroups2;
	
    public static PurelyNationalCriterion buildWithExists(Boolean exists) {
        PurelyNationalCriterion criterion = new PurelyNationalCriterion();
        criterion.setExists(exists);
        return criterion;
    }
    
    @Override
   	public List<DynamicRequirementGroup> getUnboundedGroups() {		
   		return unboundedGroups;
   	}
    
       	
}
