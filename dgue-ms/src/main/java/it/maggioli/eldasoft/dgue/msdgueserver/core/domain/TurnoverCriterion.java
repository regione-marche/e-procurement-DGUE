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
public class TurnoverCriterion extends SelectionCriterion implements UnboundedRequirementGroup {

	private BigDecimal numberFisicalYears;
	private BigDecimal averageTurnover;
	private String averageTurnoverCurrency;
	private BigDecimal amount;
	private String currency;
	private Date startDate;
	private Date endDate;
	private Integer year;
	private String businessDomainDescription;
	private BigDecimal minRequirement;					  
	private String cpvs;
	private String additionalInfo;

	private List<DynamicRequirementGroup> unboundedGroups = new ArrayList<>(5);

	public static TurnoverCriterion buildWithExists(boolean exists) {
		TurnoverCriterion criterion = new TurnoverCriterion();
		criterion.setExists(exists);
		return criterion;
	}
	
	@Override
	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		// TODO Auto-generated method stub
		return null;
	}

}
