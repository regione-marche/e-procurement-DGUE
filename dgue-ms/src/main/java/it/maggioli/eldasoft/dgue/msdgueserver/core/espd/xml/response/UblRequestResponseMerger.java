package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.OtherCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.CriteriaToEspdDocumentPopulator;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.EconomicOperatorImplTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.PartyImplTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.UblRequestResponseImporter;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectLotType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ContractFolderIDType;

/**
 * <p>
 * Class that imports a ESPD request and a ESPD response and create a new merged ESPD domain object.
 * <p/>
 * The new object includes:
 * <ul>
 * <li>Information on the EO</li>
 * <li>All mandatory Exclusion grounds</li>
 * <li>Selection criteria that where asked for in the new ESPD request that were answered in the old ESPD response</li>
 * </ul>
 * <p/>
 * Created by ratoico on 3/8/16 at 11:12 AM.
 */
@Component
public class UblRequestResponseMerger extends UblRequestResponseImporter {

	@Autowired
	public UblRequestResponseMerger(PartyImplTransformer partyImplTransformer,
			EconomicOperatorImplTransformer economicOperatorImplTransformer,
			CriteriaToEspdDocumentPopulator criteriaToEspdDocumentPopulator) {
		super(partyImplTransformer, economicOperatorImplTransformer, criteriaToEspdDocumentPopulator);
	}

	/**
	 * Build an instance of a {@link ESPDDocument} populated with data coming from a UBL Request and Response.
	 *
	 * @return A ESPD domain object with the information merge from the Request and Response
	 */
	public ESPDDocument mergeRequestAndResponse(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return buildESPDDocument(requestType, responseType);
	}

	@Override
	protected List<ContractingPartyType> provideContractingParty(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		//hotfix
		//Part 1 MUST come from the new request and not from the old response
		//return responseType.getContractingParty();
		
		return requestType.getContractingParty();
	}

	@Override
	protected List<EconomicOperatorPartyType> provideEconomicOperatorParty(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getEconomicOperatorParty();
	}

	@Override
	protected List<TenderingCriterionType> provideCriteria(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		List<TenderingCriterionType> toKeep = new ArrayList<>(responseType.getTenderingCriterion().size());

		// a request contains only exclusion and selection criteria
		// and we need to keep only the criteria specified in the request
		for (TenderingCriterionType reqCrit : requestType.getTenderingCriterion()) {
			TenderingCriterionType toAdd = null;

			// if criteria exists in response then use it
			for (TenderingCriterionType respCrit : responseType.getTenderingCriterion()) {
				if (Objects.equals(respCrit.getID().getValue(), reqCrit.getID().getValue())) {
					toAdd = respCrit;
					break;
				}
			}

			toKeep.add(toAdd == null ? reqCrit : toAdd);
		}

		// economic operator criteria are not part of the request but we will get them from the response
		for (OtherCriterion awardCrit : OtherCriterion.ALL_VALUES) {
			for (TenderingCriterionType respCrit : responseType.getTenderingCriterion()) {
				if (awardCrit.getUuid().equals(respCrit.getID().getValue())) {
					toKeep.add(respCrit);
					break;
				}
			}
		}

		return toKeep;
	}

	@Override
	protected List<ProcurementProjectLotType> provideProjectLots(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getProcurementProjectLot();
	}

	@Override
	protected List<DocumentReferenceType> provideDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		
		//TODO: combine ted info from request and request info from response here
		return responseType.getAdditionalDocumentReference();
	}

	@Override
	protected List<DocumentReferenceType> provideTedDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return requestType.getAdditionalDocumentReference();
	}

	@Override
	protected ContractFolderIDType provideContractFolder(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		//Part 1 MUST come from the new request and not from the old response
		return requestType.getContractFolderID();
	}

	@Override
	protected void addRequestInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType,
			ESPDDocument espdDocument) {
		addEspdRequestInformation(requestType, responseType, espdDocument);
	}

}
