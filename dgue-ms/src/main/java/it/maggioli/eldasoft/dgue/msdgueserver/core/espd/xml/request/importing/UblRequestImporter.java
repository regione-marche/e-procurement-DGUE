package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.importing;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.CriterionType;
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
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Create an instance of a {@link ESPDDocument} populated with data coming from a UBL {@link QualificationApplicationRequestType}.
 * <p/>
 * Created by ratoico on 11/25/15 11:28 AM.
 */
@Component
public class UblRequestImporter extends UblRequestResponseImporter {

    @Autowired
	protected UblRequestImporter(
			PartyImplTransformer partyImplTransformer,
			EconomicOperatorImplTransformer economicOperatorImplTransformer,
			CriteriaToEspdDocumentPopulator criteriaToESPDDocumentPopulator) {
		super(partyImplTransformer, economicOperatorImplTransformer, criteriaToESPDDocumentPopulator);
	}

	/**
     * Build an instance of a {@link ESPDDocument} populated with data coming from a UBL {@link QualificationApplicationRequestType}.
     *
     * @param input The XML object structure of an ESPD Request
     *
     * @return An {@link ESPDDocument} entity containing the information coming from the XML request file.
     */
    public ESPDDocument importRequest(QualificationApplicationRequestType input) {
	    ESPDDocument document = buildESPDDocument(input, null);
	    // the request information is read differently than in the case of a response
	    addEspdRequestInformation(input, null, document);
	    return document;
    }

	@Override
	protected List<ContractingPartyType> provideContractingParty(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return requestType.getContractingParty();
	}

	@Override
	protected List<EconomicOperatorPartyType> provideEconomicOperatorParty(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		// a request does not have economic operator information
		return null;
	}

	@Override
	protected List<TenderingCriterionType> provideCriteria(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return requestType.getTenderingCriterion();
	}

	@Override
	protected List<ProcurementProjectLotType> provideProjectLots(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return requestType.getProcurementProjectLot();
	}

	@Override
	protected List<DocumentReferenceType> provideDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return requestType.getAdditionalDocumentReference();
	}

	@Override
	protected List<DocumentReferenceType> provideTedDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return requestType.getAdditionalDocumentReference();
	}

	@Override
	protected ContractFolderIDType provideContractFolder(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return requestType.getContractFolderID();
	}
	
	@Override
	protected void addRequestInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType,
			ESPDDocument espdDocument) {
		addEspdRequestInformation(requestType, responseType, espdDocument);
	}

}
