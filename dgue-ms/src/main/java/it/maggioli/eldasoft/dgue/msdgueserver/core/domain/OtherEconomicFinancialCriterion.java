package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.ArrayList;
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
public class OtherEconomicFinancialCriterion extends SelectionCriterion implements UnboundedRequirementGroup {

    private List<DynamicRequirementGroup> unboundedGroups = new ArrayList<>(5);

    public static OtherEconomicFinancialCriterion buildWithExists(boolean exists) {
        OtherEconomicFinancialCriterion criterion = new OtherEconomicFinancialCriterion();
        criterion.setExists(exists);
        return criterion;
    }
    
    @Override
	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		// TODO Auto-generated method stub
		return null;
	}
}
