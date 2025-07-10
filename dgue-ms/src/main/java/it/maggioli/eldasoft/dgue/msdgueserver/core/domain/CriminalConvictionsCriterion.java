package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

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
public class CriminalConvictionsCriterion extends ExclusionCriterion implements UnboundedRequirementGroup {

   private List<DynamicRequirementGroup> unboundedGroups;

    public static CriminalConvictionsCriterion buildWithExists(Boolean exists) {
        CriminalConvictionsCriterion criminalConvictions = new CriminalConvictionsCriterion();
        criminalConvictions.setExists(exists);
        return criminalConvictions;
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
