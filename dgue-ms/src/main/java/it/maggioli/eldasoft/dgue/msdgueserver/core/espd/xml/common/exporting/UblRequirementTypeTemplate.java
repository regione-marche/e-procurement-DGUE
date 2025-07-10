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

package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;

/**
 * Creates a UBL {@link TenderingCriterionPropertyType} from the information coming from ESPD.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
public abstract class UblRequirementTypeTemplate {

	/**
	 * @param fiscalCode 		  The fiscal code or vat number of SA
	 * @param ccvRequirement      The meta definition of the requirement coming from a configuration file or eCertis
	 *                            (in the future)
	 * @param espdCriterion       The criterion containing the user information coming from the ESPD application
	 * @param requirementGroup    The parent requirement group of the requirement
	 * @param unboundedGroupIndex If the group is unbounded, we need to track its current position to be able to fetch
	 *                            its information from the ESPD criterion model
	 *
	 * @return An instance of {@link TenderingCriterionPropertyType} filled with the user information coming from the ESPD application.
	 */
	protected abstract TenderingCriterionPropertyType buildRequirementType(String fiscalCode,
																		   CacCriterionRequirement ccvRequirement,
																		   ESPDCriterion espdCriterion,
																		   CacRequirementGroup requirementGroup,
																		   DynamicRequirementGroup dynamicRequirementGroup,
																		   int unboundedGroupIndex,
																		   String evidenceCode,
																		   List<TenderingCriterionResponseType> tcrList);
	
	
	
	protected abstract TenderingCriterionResponseType buildRequirementTypeResponse(String fiscalCode,
			   CacCriterionRequirement ccvRequirement,
			   ESPDCriterion espdCriterion,
			   DynamicRequirementGroup dynamicRequirementGroup,
			   CacRequirementGroup requirementGroup,
			   int unboundedGroupIndex,
			   String evidenceCode,
			   String questionId);
}
