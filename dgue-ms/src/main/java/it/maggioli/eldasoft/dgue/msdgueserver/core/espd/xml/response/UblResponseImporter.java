package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.CriteriaToEspdDocumentPopulator;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.EconomicOperatorImplTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.PartyImplTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing.UblRequestResponseImporter;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectLotType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SignatureType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ContractFolderIDType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Create an instance of a {@link ESPDDocument} populated with data coming from a UBL {@link QualificationApplicationResponseType}.
 * <p/>
 * Created by ratoico on 1/6/16 at 5:41 PM.
 */
@Component
@Slf4j
public class UblResponseImporter extends UblRequestResponseImporter {

	@Autowired
	public UblResponseImporter(PartyImplTransformer partyImplTransformer,
			EconomicOperatorImplTransformer economicOperatorImplTransformer,
			CriteriaToEspdDocumentPopulator criteriaToEspdDocumentPopulator) {
		super(partyImplTransformer, economicOperatorImplTransformer, criteriaToEspdDocumentPopulator);
	}

	/**
	 * Build an instance of a {@link ESPDDocument} populated with data coming from a UBL {@link QualificationApplicationResponseType}.
	 *
	 * @param responseType The XML object structure of an ESPD Response
	 *
	 * @return An {@link ESPDDocument} entity containing the information coming from the XML response file.
	 */
	public ESPDDocument importResponse(QualificationApplicationResponseType responseType) {
		ESPDDocument espd = buildESPDDocument(null, responseType);
//		if (responseType.getIssueDate() != null && responseType.getIssueDate().getValue() != null) {
//			espd.setDocumentDate(responseType.getIssueDate().getValue());
//		}

		readSignatureInformation(responseType, espd);
		readConsortiumName(responseType, espd);

		return espd;
	}

	private void readSignatureInformation(QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		if (CollectionUtils.isEmpty(responseType.getSignature())) {
			return;
		}

		SignatureType signatureType = responseType.getSignature().get(0);
		PartyType signatoryParty = signatureType.getSignatoryParty();
//		if (signatoryParty == null || signatoryParty.getPhysicalLocation() == null) {
//			return;
//		}

		if (signatoryParty != null && signatoryParty.getPhysicalLocation().getName() != null) {
			espdDocument.setLocation(signatoryParty.getPhysicalLocation().getName().getValue());
		}
		
		if (signatureType != null) {
			if(signatureType.getID() != null && signatureType.getID().getValue() != null) {				
				espdDocument.setSignature(signatureType.getID().getValue());
			}
			if(signatureType.getValidationDate() != null && signatureType.getValidationDate().getValue() != null) {				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
                String strDate = dateFormat.format(toDate(signatureType.getValidationDate().getValue()));                
				espdDocument.setSignatureDate(strDate);
			}
		}
	}
	
	  public static Date toDate(XMLGregorianCalendar calendar){
	        if(calendar == null) {
	            return null;
	        }
	        return calendar.toGregorianCalendar().getTime();
	    }
	    
	
	private void readConsortiumName(QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		if (responseType.getEconomicOperatorGroupName() == null) {
			return;
		}
		//espdDocument.setConsortiumName(responseType.getEconomicOperatorGroupName().getValue());
	}

	@Override
	protected List<ContractingPartyType> provideContractingParty(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return responseType.getContractingParty();
	}

	@Override
	protected List<EconomicOperatorPartyType> provideEconomicOperatorParty(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getEconomicOperatorParty();
	}

	@Override
	protected List<TenderingCriterionType> provideCriteria(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return responseType.getTenderingCriterion();
	}

	@Override
	protected List<ProcurementProjectLotType> provideProjectLots(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getProcurementProjectLot();
	}

	@Override
	protected List<DocumentReferenceType> provideDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getAdditionalDocumentReference();
	}
	
	@Override
	protected List<DocumentReferenceType> provideTedDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType) {
		return responseType.getAdditionalDocumentReference();
	}

	@Override
	protected ContractFolderIDType provideContractFolder(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		return responseType.getContractFolderID();
	}
	
	@Override
	protected void addRequestInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType,
			ESPDDocument espdDocument) {
		List<DocumentReferenceType> documentReferences = provideDocumentReferences(requestType, responseType);
		readRequestInfo(documentReferences,espdDocument);
		
		if (StringUtils.isBlank(espdDocument.getId())) {
			log.warn("No ESPD Request information found for response '{}'.", getResponseId(responseType));
		}
	}
}
