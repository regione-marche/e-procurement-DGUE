package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.util.StringUtils;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AdditionalDocumentReference;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDRequestMetadata;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.PartyImpl;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ProcurementProjectLot;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.DocumentTypeCode;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectLotType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ContractFolderIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Created by ratoico on 3/9/16 at 2:08 PM.
 */
@Slf4j
public abstract class UblRequestResponseImporter {

	private final PartyImplTransformer partyImplTransformer;
	private final EconomicOperatorImplTransformer economicOperatorImplTransformer;
	private final CriteriaToEspdDocumentPopulator criteriaToESPDDocumentPopulator;

	protected UblRequestResponseImporter(PartyImplTransformer partyImplTransformer,
			EconomicOperatorImplTransformer economicOperatorImplTransformer,
			CriteriaToEspdDocumentPopulator criteriaToESPDDocumentPopulator) {
		this.partyImplTransformer = partyImplTransformer;
		this.economicOperatorImplTransformer = economicOperatorImplTransformer;
		this.criteriaToESPDDocumentPopulator = criteriaToESPDDocumentPopulator;
	}

	/**
	 * Build an instance of a {@link ESPDDocument} populated with data coming from a UBL {@link QualificationApplicationRequestType}, a
	 * {@link QualificationApplicationResponseType} or a combination of the two of them (in the case of a merge).
	 *
	 * @param requestType a UBL {@link QualificationApplicationRequestType}
	 * @param responseType a UBL {@link QualificationApplicationResponseType}
	 */
	protected final ESPDDocument buildESPDDocument(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
		ESPDDocument espdDocument = new ESPDDocument();
		addOwnerInformation(requestType, responseType, espdDocument);
		addPartyInformation(requestType, responseType, espdDocument);
		addCriteriaInformation(requestType, responseType, espdDocument);
		addOtherInformation(requestType, responseType, espdDocument);
		return espdDocument;
	}

	private void addOwnerInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		if(requestType != null && requestType.getID() != null && requestType.getID().getSchemeName() != null){
			espdDocument.setOwner(requestType.getID().getSchemeName());
		}

		if(responseType != null && responseType.getID() != null && responseType.getID().getSchemeName() != null){
			espdDocument.setOwner(responseType.getID().getSchemeName());
		}

	}

	private void addPartyInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		List<ContractingPartyType> caParty = provideContractingParty(requestType, responseType);
		if (caParty != null && caParty.size() > 0 && caParty.get(0).getParty() != null) {
			PartyImpl authority = partyImplTransformer.apply(caParty.get(0).getParty());
			espdDocument.setAuthority(authority);
			
		}

		List<EconomicOperatorPartyType> economicOperatorParty = provideEconomicOperatorParty(requestType, responseType);
		if (economicOperatorParty != null) {
			espdDocument.setEconomicOperator(economicOperatorImplTransformer.buildEconomicOperator(
					economicOperatorParty.get(0)));
		}
	}

	private void addCriteriaInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		
		criteriaToESPDDocumentPopulator.addCriteriaToEspdDocument(espdDocument, provideCriteria(requestType, responseType),requestType,responseType);
		
		
	}

	private void addOtherInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		addProjectLotInformation(requestType, responseType, espdDocument);		
		addRequestInformation(requestType, responseType, espdDocument);
		//DA VERIFICARE SE TENERE
		//addEspdRequestInformation(requestType,responseType,espdDocument);
		addTedInformation(requestType, responseType, espdDocument);
		//addNgojInformation(requestType, responseType, espdDocument);		
		addOther(requestType, responseType, espdDocument);
		addProfileURI(requestType, responseType, espdDocument);
	}
	
	
	private void addProfileURI(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		if(requestType!= null && requestType.getContractingParty() != null && requestType.getContractingParty().size() > 0 && requestType.getContractingParty().get(0).getBuyerProfileURI()!= null) {		
			espdDocument.getAuthority().setProfileURI(requestType.getContractingParty().get(0).getBuyerProfileURI().getValue());
		}
		if(responseType!= null && responseType.getContractingParty() != null && responseType.getContractingParty().size() > 0 && responseType.getContractingParty().get(0).getBuyerProfileURI()!= null) {		
			espdDocument.getAuthority().setProfileURI(responseType.getContractingParty().get(0).getBuyerProfileURI().getValue());
		}
	}
	
//	private void addProjectLotInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
//		List<ProcurementProjectLotType> projectLots = provideProjectLots(requestType, responseType);
//		
//		List<ProcurementProjectLot> lots=new ArrayList<ProcurementProjectLot>();
//		
//		Long l = 1L;
//		for (ProcurementProjectLotType procurementProjectLot : projectLots) {
//			ProcurementProjectLot ppl=new ProcurementProjectLot();
//			if(procurementProjectLot.getID().getValue().contains("_")) {
//				
//				String[] parts = procurementProjectLot.getID().getValue().split("_");
//				if(parts != null && parts.length > 1) {							
//					ppl.setNumLot(parts[0]);
//					ppl.setCigLot(parts[1]);
//					lots.add(ppl);
//				}
//			}else {
//				ppl.setNumLot(l.toString());
//				ppl.setCigLot(procurementProjectLot.getID().getValue());
//				lots.add(ppl);
//			}
//			l++;
//			
//		}
//		espdDocument.setLots(lots);
//	}
	
	private void addProjectLotInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		List<ProcurementProjectLotType> projectLots = provideProjectLots(requestType, responseType);
		
		List<ProcurementProjectLot> lots=new ArrayList<ProcurementProjectLot>();
				
		for (ProcurementProjectLotType procurementProjectLot : projectLots) {
			if(procurementProjectLot != null) {
				ProcurementProjectLot ppl=new ProcurementProjectLot();
				
				if(procurementProjectLot.getID().getValue().contains("_")) {
					String[] parts = procurementProjectLot.getID().getValue().split("_");
					if(parts != null && parts.length > 1) {							
						ppl.setNumLot(parts[0]);						
						lots.add(ppl);
					}
				} else if(procurementProjectLot.getID().getValue() != null ) {							
					ppl.setNumLot(procurementProjectLot.getID().getValue());					
					lots.add(ppl);
				}
			}
			
			
		}
		espdDocument.setLots(lots);
	}

	private void addOther(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		String procedureCode = "";
		String procedureTitle = "";
		String procedureShortDesc = "";
		String projectType = "";
		String codiceANAC = "";
		if(requestType != null) {
			
			procedureCode=requestType.getProcedureCode().getValue();
			espdDocument.setProcedureCode(procedureCode);
			
			
			if(requestType.getProcurementProject() != null) {
				if(requestType.getProcurementProject().getName() != null && requestType.getProcurementProject().getName().size() > 0) {
					procedureTitle=requestType.getProcurementProject().getName().get(0).getValue();
					espdDocument.setProcedureTitle(procedureTitle);
				}
				
				
				if(requestType.getProcurementProject().getDescription() != null && requestType.getProcurementProject().getDescription().size() > 0) {
					procedureShortDesc=requestType.getProcurementProject().getDescription().get(0).getValue();
					espdDocument.setProcedureShortDesc(procedureShortDesc);
					
				}
				
				if(requestType.getProcurementProject().getProcurementTypeCode() != null && requestType.getProcurementProject().getProcurementTypeCode().getValue() != null) {
					projectType=requestType.getProcurementProject().getProcurementTypeCode().getValue();
					espdDocument.setProjectType(projectType);					
				}
				
				if(requestType.getProcurementProject().getMainCommodityClassification() != null && requestType.getProcurementProject().getMainCommodityClassification().size() > 0) {
					List<String> cpvs = new ArrayList<String>();
					for (CommodityClassificationType item : requestType.getProcurementProject().getMainCommodityClassification()) {
						if(item.getItemClassificationCode() != null && item.getItemClassificationCode().getValue() != null) {
							cpvs.add(item.getItemClassificationCode().getValue());
						}
					}
					espdDocument.setCpvs(cpvs);			
				}
				
				if(requestType.getProcurementProject().getID() != null && requestType.getProcurementProject().getID().getValue() != null) {
					codiceANAC = requestType.getProcurementProject().getID().getValue();
					espdDocument.setCodiceANAC(codiceANAC);				
				}
				
			}
			
		
			
			String fileRefByCA = "";
			fileRefByCA=requestType.getContractFolderID().getValue();
			espdDocument.setFileRefByCA(fileRefByCA);
			
		}
		
		if(responseType != null) {
			if(responseType.getProcedureCode() != null) {
				
				procedureCode=responseType.getProcedureCode().getValue();
				espdDocument.setProcedureCode(procedureCode);
				
				if(responseType.getProcurementProject() != null) {
					if(responseType.getProcurementProject().getName() != null && responseType.getProcurementProject().getName().size() > 0) {
						procedureTitle=responseType.getProcurementProject().getName().get(0).getValue();
						espdDocument.setProcedureTitle(procedureTitle);
					}
					
					
					if(responseType.getProcurementProject().getDescription() != null && responseType.getProcurementProject().getDescription().size() > 0) {
						procedureShortDesc=responseType.getProcurementProject().getDescription().get(0).getValue();
						espdDocument.setProcedureShortDesc(procedureShortDesc);				
					}
					
					if(responseType.getProcurementProject().getProcurementTypeCode() != null && responseType.getProcurementProject().getProcurementTypeCode().getValue() != null) {
						projectType=responseType.getProcurementProject().getProcurementTypeCode().getValue();
						espdDocument.setProjectType(projectType);					
					}
					
					if(responseType.getProcurementProject().getID() != null && responseType.getProcurementProject().getID().getValue() != null) {
						codiceANAC = responseType.getProcurementProject().getID().getValue();
						espdDocument.setCodiceANAC(codiceANAC);				
					}
					
				}
				
				
				String fileRefByCA = "";
				if(responseType.getContractFolderID() != null) {				
					fileRefByCA=responseType.getContractFolderID().getValue();
				}
				espdDocument.setFileRefByCA(fileRefByCA);
				
				
				
			}
		}
	}
	
	protected final void addEspdRequestInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType,ESPDDocument espdDocument) {
		
		ESPDRequestMetadata metadata = new ESPDRequestMetadata();
		if(requestType != null) {				
			
			espdDocument.setId(requestType.getID().getValue());
			espdDocument.setUUID(requestType.getUUID().getValue());
	        if(requestType.getIssueDate() != null) {     
	        	
	        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
                String strDate = dateFormat.format(toDate(requestType.getIssueDate().getValue()));                
                espdDocument.setIssueDate(strDate);
	        					
	        }
	        if(requestType.getIssueTime() != null) {        
	        	espdDocument.setIssueTime(requestType.getIssueTime().getValue().toString());
	        }
	       
		} /*else if(responseType != null) {
				List<DocumentReferenceType> documentReferences = provideTedDocumentReferences(requestType, responseType);
				if (documentReferences != null && documentReferences.size() > 0) {
					for (DocumentReferenceType documentReferenceType : documentReferences) {
						if(documentReferenceType != null) {	
							if(documentReferenceType.getDocumentTypeCode().getValue().equals("ESPD_REQUEST")) {
								if(documentReferenceType.getID() != null) {
									espdDocument.setId(documentReferenceType.getID().getValue());
								}
								
								if(documentReferenceType.getUUID() != null) {
									espdDocument.setUUID(documentReferenceType.getUUID().getValue());
								}
								
								if(documentReferenceType.getIssueDate() != null) {
									DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
					                String strDate = dateFormat.format(toDate(documentReferenceType.getIssueDate().getValue()));                
									espdDocument.setIssueDate(strDate);
									
								}
								
								if(documentReferenceType.getIssueTime() != null) {
									espdDocument.setIssueTime(documentReferenceType.getIssueTime().getValue().toString());
								}
								
							}
							break;						
						}
					}
				}								
		       
		}*/
    }
	
	 public static Date toDate(XMLGregorianCalendar calendar){
	        if(calendar == null) {
	            return null;
	        }
	        return calendar.toGregorianCalendar().getTime();
	    }
	    
	
	protected final void readRequestInfo(List<DocumentReferenceType> documentReferenceTypes, ESPDDocument espdDocument) {
		
		List<DocumentReferenceType> requestReferences = UblDocumentReferences
				.filterByTypeCode(documentReferenceTypes, DocumentTypeCode.ESPD_REQUEST);
		if (requestReferences != null && requestReferences.size() > 0) {
			DocumentReferenceType requestInfo = requestReferences.get(0);
			espdDocument.setId(UblDocumentReferences.readIdValue(requestInfo));
			espdDocument.setIssueDate(readIssueDate(requestInfo.getIssueDate()));
			espdDocument.setIssueTime(UblDocumentReferences.readIssueTimeValue(requestInfo));
			espdDocument.setUUID(UblDocumentReferences.readUUIDValue(requestInfo));
		}
	}
	
   

	protected final String readIssueDate(IssueDateType issueDateType) {
		if (issueDateType == null || issueDateType.getValue() == null) {
			return null;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String strDate = dateFormat.format(toDate(issueDateType.getValue()));
        return strDate;
	}
	
	private void addTedInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument) {
		ContractFolderIDType contractFolder = provideContractFolder(requestType, responseType);
		if (contractFolder != null) {
			espdDocument.setFileRefByCA(contractFolder.getValue());
		}

		List<DocumentReferenceType> documentReferences = provideTedDocumentReferences(requestType, responseType);
		List<AdditionalDocumentReference> additionalDocumentReference = new ArrayList<AdditionalDocumentReference>();
		if (documentReferences != null && documentReferences.size() > 0) {
			for (DocumentReferenceType documentReferenceType : documentReferences) {
				if(documentReferenceType != null) {					
					AdditionalDocumentReference adr=new AdditionalDocumentReference();
					if(documentReferenceType.getDocumentTypeCode() != null && documentReferenceType.getDocumentTypeCode().getValue() != null) {
						
						adr.setDocTypeCode(documentReferenceType.getDocumentTypeCode().getValue());
					}
					if(documentReferenceType.getID() != null && documentReferenceType.getID().getValue() != null) {
						
						adr.setNojcnNumber(documentReferenceType.getID().getValue());
					}
					if(documentReferenceType.getAttachment() != null && documentReferenceType.getAttachment().getExternalReference() != null) {					
						if(documentReferenceType.getAttachment().getExternalReference().getFileName() != null && documentReferenceType.getAttachment().getExternalReference().getFileName().getValue() != null) {
							
							adr.setTitle(documentReferenceType.getAttachment().getExternalReference().getFileName().getValue());
						}
						if(documentReferenceType.getAttachment().getExternalReference().getDescription() != null && documentReferenceType.getAttachment().getExternalReference().getDescription().size() > 0 && documentReferenceType.getAttachment().getExternalReference().getDescription().get(0) != null) {
							
							adr.setDescr(documentReferenceType.getAttachment().getExternalReference().getDescription().get(0).getValue());
						}
						if(documentReferenceType.getAttachment().getExternalReference().getDescription() != null && documentReferenceType.getAttachment().getExternalReference().getDescription().size() > 1 && documentReferenceType.getAttachment().getExternalReference().getDescription().get(1) != null) {
							
							adr.setTedReceptionId(documentReferenceType.getAttachment().getExternalReference().getDescription().get(1).getValue());
						}
						if(documentReferenceType.getAttachment().getExternalReference().getURI() != null && documentReferenceType.getAttachment().getExternalReference().getURI().getValue() != null) {
							
							adr.setTedUrl(documentReferenceType.getAttachment().getExternalReference().getURI().getValue());
						}
	
					}
					additionalDocumentReference.add(adr);
				}
			}

			espdDocument.setAdditionalDocumentReference(additionalDocumentReference);
			
			
//			espdDocument.setProcedureTitle(UblDocumentReferences.readFileNameValue(procurementInfo));
//			espdDocument.setProcedureShortDesc(UblDocumentReferences.readDescriptionValue(procurementInfo));
		} else {
			log.warn("No TED information found for response '{}'.", getResponseId(responseType));
		}

	}

	
	
	protected String getResponseId(QualificationApplicationResponseType input) {
		if (input == null || input.getID() == null) {
			return "";
		}
		return input.getID().getValue();
	}

	private String readLot(List<ProcurementProjectLotType> lots) {
		if (lots.size() < 0) {
			return null;
		}

		ProcurementProjectLotType lotType = lots.get(0);
		if (lotType.getID() != null && !"0".equals(lotType.getID().getValue())) {
			return lotType.getID().getValue();
		}

		return null;
	}

	protected abstract void addRequestInformation(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType, ESPDDocument espdDocument);

	/**
	 * Provide the list of UBL elements of request document containing information about the additional document references type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A list of {@link DocumentReferenceType} element
	 */
	protected abstract List<DocumentReferenceType> provideTedDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);

	/**
	 * Provide the UBL element containing information about the contracting authority party type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A {@link ContractingPartyType} element
	 */
	protected abstract List<ContractingPartyType> provideContractingParty(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);

	/**
	 * Provide the UBL element containing information about the economic operator party type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A {@link EconomicOperatorPartyType} element
	 */
	protected abstract List<EconomicOperatorPartyType> provideEconomicOperatorParty(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);

	/**
	 * Provide the list of UBL criteria to be parsed and set onto the ESPD domain object.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A list of UBL criteria to be parsed into the ESPD domain object.
	 */
	protected abstract List<TenderingCriterionType> provideCriteria(QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType);

	/**
	 * Provide the list of UBL elements containing information about the procurement project lots type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A list of {@link ProcurementProjectLotType} element
	 */
	protected abstract List<ProcurementProjectLotType> provideProjectLots(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);

	/**
	 * Provide the list of UBL elements containing information about the additional document references type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A list of {@link DocumentReferenceType} element
	 */
	protected abstract List<DocumentReferenceType> provideDocumentReferences(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);
	
	/**
	 * Provide the UBL element containing information about the contract folder id type.
	 *
	 * @param requestType  A UBL {@link QualificationApplicationRequestType}
	 * @param responseType A UBL {@link QualificationApplicationResponseType}
	 *
	 * @return A {@link ContractFolderIDType} element
	 */
	protected abstract ContractFolderIDType provideContractFolder(QualificationApplicationRequestType requestType,
			QualificationApplicationResponseType responseType);
}
