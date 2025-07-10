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

import java.math.BigDecimal;
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
public class TechnicalProfessionalCriterion extends SelectionCriterion implements UnboundedRequirementGroup {

	private BigDecimal percentage;
	private String specify;
	private Object requirement;
	private Object lotId;
	private BigDecimal minReferences;
	private BigDecimal minNumberYear;
	private String additionalInfo;
	private Boolean weightedCriterion;
	private BigDecimal share;
	private List<DynamicRequirementGroup> unboundedGroups = new ArrayList<>();
	private List<DynamicRequirementGroup> unboundedGroups2 = new ArrayList<>();	

	public static TechnicalProfessionalCriterion buildWithExists(boolean exists) {
		TechnicalProfessionalCriterion criterion = new TechnicalProfessionalCriterion();
		criterion.setExists(exists);
		return criterion;
	}

	@Override
	public List<DynamicRequirementGroup> getUnboundedGroups() {
		return this.unboundedGroups;
	}
	
	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		return this.unboundedGroups2;
	}
	
	
	
	
	
	

}
