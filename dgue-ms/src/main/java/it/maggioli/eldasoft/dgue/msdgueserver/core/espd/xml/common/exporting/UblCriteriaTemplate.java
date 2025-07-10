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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SatisfiesAllCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.ExclusionCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.OtherCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.SelectionCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

import org.springframework.util.CollectionUtils;

/**
 * Creates a list of UBL {@link TenderingCriterionType} elements to be populated in a ESPD Request or Response.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Slf4j
public abstract class UblCriteriaTemplate {

	/**
	 * Builds a list of UBL {@link TenderingCriterionType} elements.
	 *
	 * @param espdDocument The input coming from the ESPD application
	 *
	 * @return The list of UBL criteria
	 */
	
	
	
	public List<TenderingCriterionType> apply(ESPDDocument espdDocument,QualificationApplicationResponseType responseType,boolean alsoResponse,
			List<TenderingCriterionResponseType> tcr) {
		List<TenderingCriterionType> criterionTypes = new ArrayList<>(
				ExclusionCriterion.ALL_VALUES.size() + SelectionCriterion.ALL_VALUES.size() + 1);
		// THE ORDER OF CRITERIA IS VERY IMPORTANT AND IT SHOULD BE COVERED BY THE TESTS!!!
		criterionTypes.addAll(addExclusionCriteria(espdDocument,responseType,alsoResponse,tcr));
		criterionTypes.addAll(addSelectionCriteria(espdDocument,responseType,alsoResponse,tcr));
		criterionTypes.addAll(addAwardCriteria(espdDocument,responseType,alsoResponse,tcr));
		return Collections.unmodifiableList(criterionTypes);
	}

	private List<TenderingCriterionType> addExclusionCriteria(ESPDDocument espdDocument,QualificationApplicationResponseType responseType,
			boolean alsoResponse, List<TenderingCriterionResponseType> tcr) {
		List<TenderingCriterionType> criterionTypes = new ArrayList<>(ExclusionCriterion.ALL_VALUES.size() + 1);
		// All exclusion criteria except 'Purely national grounds' must be present no matter the existence
		for (ExclusionCriterion criterion : ExclusionCriterion.ALL_VALUES) {
			addSelectedUblCriterion(criterion, espdDocument, criterionTypes,responseType,alsoResponse,tcr);
		}
        if (CollectionUtils.isEmpty(criterionTypes)) {
		    log.warn("ESPD document does not contain any exclusion criteria.");
        }
		return criterionTypes;
	}

	private List<TenderingCriterionType> addSelectionCriteria(ESPDDocument espdDocument,QualificationApplicationResponseType responseType,
			boolean alsoResponse, List<TenderingCriterionResponseType> tcr) {

		List<TenderingCriterionType> criterionTypes = new ArrayList<>(SelectionCriterion.ALL_VALUES.size() + 1);
	/*	if (!espdDocument.getAtLeastOneSelectionCriterionWasSelected()) {
			// Option 3:
			// CA selects no selection criteria -> EO sees all selection criteria (including "All selection criteria")
			for (SelectionCriterion criterion : SelectionCriterion.ALL_VALUES) {
				addAlwaysUblCriterion(criterion, espdDocument, criterionTypes,responseType,alsoResponse,tcr);
			}
		} else if (satisfiesAllCriterionPresent(espdDocument.getSelectionSatisfiesAll())) {
			// Option 1:
			// CA selects "All section criteria" -> EO sees only "All selection criteria" and not the individual ones.
			addSelectedUblCriterion(SelectionCriterion.ALL_SELECTION_CRITERIA_SATISFIED, espdDocument, criterionTypes,responseType,alsoResponse,tcr);
		} else {*/
			// Option 2:
			// CA select individual selection criteria -> EO sees only the selected ones (and even not the "All selection criteria")
			for (SelectionCriterion criterion : SelectionCriterion.ALL_VALUES) {
				// this will also cover the case when 'Satisfies all' exists with the answer 'No'
				addSelectedUblCriterion(criterion, espdDocument, criterionTypes,responseType,alsoResponse,tcr);
			}
	/*	}*/
        if (CollectionUtils.isEmpty(criterionTypes)) {
            log.warn("ESPD document does not contain any selection criteria.");
        }
		return criterionTypes;
	}

	private List<TenderingCriterionType> addAwardCriteria(ESPDDocument espdDocument,QualificationApplicationResponseType responseType,
			boolean alsoResponse, List<TenderingCriterionResponseType> tcr) {
		List<TenderingCriterionType> criterionTypes = new ArrayList<>(OtherCriterion.ALL_VALUES.size() + 1);
		// All exclusion criteria except 'Purely national grounds' must be present no matter the existence
		for (OtherCriterion criterion : OtherCriterion.ALL_VALUES) {
			addAlwaysUblCriterion(criterion, espdDocument, criterionTypes,responseType,alsoResponse,tcr);
		}
		return criterionTypes;
	}

	/**
	 * Add a UBL criterion only if it was selected (exists) by the CA.
	 *
	 * @param cacCriterion The criterion metadata
	 * @param espdDocument The model coming from ESPD
	 * @param ublCriteria  The list of UBL criteria on which we add the ESPD criteria
	 */
	private void addSelectedUblCriterion(CacCriterion cacCriterion, ESPDDocument espdDocument,
										 List<TenderingCriterionType> ublCriteria,QualificationApplicationResponseType responseType,
										 boolean alsoResponse, List<TenderingCriterionResponseType> tcr) {
		ESPDCriterion espdCriterion = espdDocument.readCriterionFromEspd(cacCriterion);
		if (isCriterionSelectedByTheCA(espdCriterion)) {
			if(espdDocument.getAuthority() != null) {		
				ublCriteria.add(getCriterionTransformer().buildCriterionType(espdDocument.getAuthority().getVatNumber(), cacCriterion, espdCriterion,responseType,alsoResponse,tcr));
			}else {
				ublCriteria.add(getCriterionTransformer().buildCriterionType(espdDocument.getEconomicOperator().getVatNumber(), cacCriterion, espdCriterion,responseType,alsoResponse,tcr));
			}
							
		}
	}

	protected final boolean isCriterionSelectedByTheCA(ESPDCriterion espdCriterion) {
		return espdCriterion != null && espdCriterion.getExists();
	}

	/**
	 * Add a UBL criterion no matter the exists flag (needed by award criteria which always need to be present).
	 *
	 * @param cacCriterion The criterion metadata
	 * @param ublCriteria  The list of UBL criteria on which we add the ESPD criteria
	 */
	private void addAlwaysUblCriterion(CacCriterion cacCriterion, ESPDDocument espdDocument,
			List<TenderingCriterionType> ublCriteria,QualificationApplicationResponseType responseType,
			boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		ESPDCriterion espdCriterion = espdDocument.readCriterionFromEspd(cacCriterion);
		if(espdDocument.getAuthority() != null) {		
			ublCriteria.add(getCriterionTransformer().buildCriterionType(espdDocument.getAuthority().getVatNumber(), cacCriterion, espdCriterion,responseType,alsoResponse,tcr));
		}else {
			ublCriteria.add(getCriterionTransformer().buildCriterionType(espdDocument.getEconomicOperator().getVatNumber(), cacCriterion, espdCriterion,responseType,alsoResponse,tcr));
		}
	}
	


	/**
	 * Construct a class that can build an UBL {@link TenderingCriterionType}.
	 *
	 * @return An instance of a class that can build {@link TenderingCriterionType}
	 */
	protected abstract UblCriterionTypeTemplate getCriterionTransformer();

	/**
	 * The logic to decide if the 'Satisfies all' selection criterion is present. The presence or absence of this
	 * criterion has an impact on the presence or absence of the other selection criteria as follows:
	 * <ol>
	 * <li>CA selects "All section criteria" -> EO sees only "All selection criteria" and not the individual ones;</li>
	 * <li>CA select individual selection criteria -> EO sees only the selected ones (and even not the "All selection criteria");</li>
	 * <li>CA selects no selection criteria -> EO sees all selection criteria (including "All selection criteria").</li>
	 * </ol>
	 *
	 * @param satisfiesAllCriterion The 'Satisfies all' criterion coming from the ESPD web application model
	 *
	 * @return True if the 'Satisfies all' is present according to the logic, false otherwise.
	 */
	protected abstract Boolean satisfiesAllCriterionPresent(SatisfiesAllCriterion satisfiesAllCriterion);

}
