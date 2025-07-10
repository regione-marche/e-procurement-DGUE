/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.ArrayList;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
public class QualityAssuranceCriterion extends SelectionCriterion  implements UnboundedRequirementGroup{

	private List<DynamicRequirementGroup> unboundedGroups = new ArrayList<>();
	
    public static QualityAssuranceCriterion buildWithExists(boolean exists) {
        QualityAssuranceCriterion criterion = new QualityAssuranceCriterion();
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
