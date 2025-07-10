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

package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;

/**
 * Interface marking requirement groups which can grow indefinitely and can have dynamic requirements. Examples of
 * such groups can be found in the Sections B or C of the selection criteria, i.e. yearly turnovers, financial ratios,
 * work contracts, supply contracts, service contracts, etc.
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
public interface UnboundedRequirementGroup {

	List<DynamicRequirementGroup> getUnboundedGroups();
	
	List<DynamicRequirementGroup> getUnboundedGroups2();
}
