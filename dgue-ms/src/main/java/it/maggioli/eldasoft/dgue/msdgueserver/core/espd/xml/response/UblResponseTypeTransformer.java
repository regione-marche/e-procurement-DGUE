package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AdditionalDocumentReference;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.QualificationApplicationType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.util.EspdConfiguration;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.CommonUblFactory;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UBLAdditionalDocumentReferenceTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblContractingPartyTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblEconomicOperatorPartyTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblProcurementProjectTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting.UblRequestCriteriaTransformer;
import lombok.extern.log4j.Log4j2;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CapabilityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.QualifyingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SignatureType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EconomicOperatorGroupNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EmployeeQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IndustryClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ItemClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValidationDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueAmountType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;


/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */	
@Component
@Log4j2
public class UblResponseTypeTransformer {

	 private final UblContractingPartyTypeTransformer contractingPartyTransformer;
	    private final UblProcurementProjectTypeTransformer procurementProjectTypeTransformer;
	    private final UBLAdditionalDocumentReferenceTypeTransformer documentReferenceTypeTransformer;
	    private final UblResponseCriteriaTransformer criteriaTransformer;	    
	    private final UblRequestCriteriaTransformer criteriaTransformerRequest;
	    private final EspdConfiguration espdConfiguration;
	    private final UblEconomicOperatorPartyTypeTransformer economicOperatorPartyTypeTransformer;

	    /**
	     *
	     * @param contractingPartyTransformer
	     * @param procurementProjectTypeTransformer
	     * @param documentReferenceTypeTransformer
	     * @param criteriaTransformer
	     * @param espdConfiguration
	     */
	    @Autowired
	    UblResponseTypeTransformer(UblContractingPartyTypeTransformer contractingPartyTransformer,
	                              UblProcurementProjectTypeTransformer procurementProjectTypeTransformer,
	                              UBLAdditionalDocumentReferenceTypeTransformer documentReferenceTypeTransformer,
	                              UblResponseCriteriaTransformer criteriaTransformer,
	                              EspdConfiguration espdConfiguration,
	                              UblEconomicOperatorPartyTypeTransformer economicOperatorPartyTypeTransformer,
	                              UblRequestCriteriaTransformer criteriaTransformerRequest) {
	        this.economicOperatorPartyTypeTransformer = economicOperatorPartyTypeTransformer;
			this.contractingPartyTransformer = contractingPartyTransformer;
	        this.procurementProjectTypeTransformer = procurementProjectTypeTransformer;
	        this.documentReferenceTypeTransformer = documentReferenceTypeTransformer;
	        this.criteriaTransformer = criteriaTransformer;
	        this.espdConfiguration = espdConfiguration;
	        this.criteriaTransformerRequest = criteriaTransformerRequest;
	    }

    
    public QualificationApplicationResponseType buildResponseType(ESPDDocument ESPDDocument) {
    	QualificationApplicationResponseType responseType=new QualificationApplicationResponseType();
    	addUBLVersionInformation(responseType);
    	addUUIDInformation(ESPDDocument,responseType);
        addCustomizationInformation(responseType);
        addIdInformation(ESPDDocument,responseType);
        addCopyIndicatorInformation(responseType);
        addVersionIdInformation(responseType);
        addIssueDateAndTimeInformation(ESPDDocument, responseType);
        addProcedureCodeInformation(ESPDDocument, responseType);
        addQualificationApplicationTypeCodeInformation(ESPDDocument, responseType);
        
        addContractFolderIdInformation(ESPDDocument, responseType);
        addPartyInformation(ESPDDocument, responseType);
        addQualifyingParty(ESPDDocument, responseType);
        addProcedureCodeInformation(ESPDDocument, responseType);
        addProcurementProject(ESPDDocument, responseType);
        addProcurementProjectLots(ESPDDocument, responseType);
        addAdditionalDocumentReference(ESPDDocument, responseType);
        
        addCriteria(ESPDDocument, responseType);
        addSignatureInformation(ESPDDocument, responseType);
        addConsortiumName(ESPDDocument, responseType);
        
        return responseType;
    }
    
    private void addProcedureCodeInformation(final ESPDDocument espdDocument, final QualificationApplicationResponseType responseType) {
    	responseType.setProcedureCode(CommonUblFactory.buildProcedureCodeType(espdDocument.getProcedureCode()));
    }

    private void addUBLVersionInformation(QualificationApplicationResponseType responseType) {
        responseType.setUBLVersionID(CommonUblFactory.buildUblVersionIDType());
    }

    private void addCustomizationInformation(QualificationApplicationResponseType responseType) {
        responseType
                .setCustomizationID(CommonUblFactory.buildCustomizationIDType(CommonUblFactory.EspdType.ESPD_RESPONSE));
    }
    
    private void addUUIDInformation(final ESPDDocument espdDocument, final QualificationApplicationResponseType responseType) {
    	String fiscalCode = ""; 
    	if (espdDocument.getAuthority() != null) {
    		fiscalCode = espdDocument.getAuthority().getVatNumber();
        }else if (espdDocument.getEconomicOperator() != null) {
        	 fiscalCode = espdDocument.getEconomicOperator().getVatNumber();
        }
    	responseType.setUUID(CommonUblFactory.buildUUIDType(fiscalCode,null));
    }

    private void addIdInformation(ESPDDocument espdDocument,QualificationApplicationResponseType responseType) {
    	String fiscalCode = ""; 
    	if (espdDocument.getAuthority() != null) {
    		fiscalCode = espdDocument.getAuthority().getVatNumber();
        }else if (espdDocument.getEconomicOperator() != null) {
        	 fiscalCode = espdDocument.getEconomicOperator().getVatNumber();
        }
        responseType.setID(CommonUblFactory.buildDocumentIdentifierType(fiscalCode));
    }

    private void addCopyIndicatorInformation(QualificationApplicationResponseType responseType) {
        responseType.setCopyIndicator(CommonUblFactory.buildCopyIndicatorType(false));
    }

    private void addVersionIdInformation(QualificationApplicationResponseType responseType) {
        //responseType.setVersionID(CommonUblFactory.buildVersionIDType(espdConfiguration.getExchangeModelVersion()));
    	responseType.setVersionID(CommonUblFactory.buildVersionIDType("1.0"));
    }
    
   
    
    private void addProcurementProject(final ESPDDocument espdDocument, final QualificationApplicationResponseType responseType) {
    	ProcurementProjectType ppt=new ProcurementProjectType();
    	if(espdDocument.getCpvs() != null && espdDocument.getCpvs().size() > 0) {
    		
    		for (String cpv : espdDocument.getCpvs()) {
    			CommodityClassificationType cct=new CommodityClassificationType();
    			ItemClassificationCodeType ict=new ItemClassificationCodeType();
    			ict.setValue(cpv);
    			cct.setItemClassificationCode(ict);
    			ppt.getMainCommodityClassification().add(cct);
    		}
    	}
    	
    	
    	
    	responseType.setProcurementProject(ppt);
    	NameType nt=new NameType();
    	nt.setValue(espdDocument.getProcedureTitle());
    	responseType.getProcurementProject().getName().add(nt);
    	DescriptionType dt=new DescriptionType();
    	dt.setValue(espdDocument.getProcedureShortDesc());
    	
    	IDType id = new IDType();
    	if(espdDocument.getCodiceANAC() != null) {  
        	id.setSchemeAgencyID("EU-COM-GROW");
        	id.setSchemeVersionID("2.1.1");
        	id.setValue(espdDocument.getCodiceANAC());
        	responseType.getProcurementProject().setID(id);
    	}
    	
    	responseType.getProcurementProject().getDescription().add(dt);
    	responseType.getProcurementProject().setProcurementTypeCode(CommonUblFactory.buildProjectCodeType(espdDocument.getProjectType()));
    	
    }

    private void addIssueDateAndTimeInformation(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
    	try {
	        if(ESPDDocument.getIssueTime() != null && ESPDDocument.getIssueDate() != null) {
	        	
		        	IssueTimeType it=new IssueTimeType();
		        	GregorianCalendar cal = new GregorianCalendar();
		        	Date date1;				
					date1 = new SimpleDateFormat("hh:mm:ss").parse(ESPDDocument.getIssueTime());				 
		        	cal.setTime(date1);
		        	it.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
		        	
		        	
		        	IssueDateType iDate=new IssueDateType();
		        	GregorianCalendar cal2 = new GregorianCalendar();
		        	Date date2 =  new SimpleDateFormat("yyyy-MM-dd").parse(ESPDDocument.getIssueDate());
		        	//Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(ESPDDocument.getIssueDate()); 
		        	cal2.setTime(date2);
		        	iDate.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2));
		        	
		        	responseType.setIssueTime(it);
		        	responseType.setIssueDate(iDate);
	        	
	        }else {
	        	 final Instant now = Instant.now();
	             try {
	            	 responseType.setIssueTime(CommonUblFactory.buildIssueTimeType(now));
	            	 responseType.setIssueDate(CommonUblFactory.buildIssueDateType(now));
	             } catch (DatatypeConfigurationException e) {
	                 log.error("Exception during set issue time or issue date", e);
	             }
	        }
	    } catch (ParseException | DatatypeConfigurationException e) {
			log.error("",e);
		} 
    }

    private void addContractFolderIdInformation(ESPDDocument espdDocument, QualificationApplicationResponseType responseType) {
    	String fiscalCode = ""; 
    	if (espdDocument.getAuthority() != null) {
    		fiscalCode = espdDocument.getAuthority().getVatNumber();
        }else if (espdDocument.getEconomicOperator() != null) {
        	 fiscalCode = espdDocument.getEconomicOperator().getVatNumber();
        }
        responseType.setContractFolderID(CommonUblFactory.buildContractFolderType(fiscalCode,espdDocument.getFileRefByCA()));
    }

    private void addPartyInformation(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
        if (ESPDDocument.getAuthority() != null) {
            ContractingPartyType contractingPartyType = contractingPartyTransformer.apply(ESPDDocument.getAuthority());
            responseType.getContractingParty().add(contractingPartyType);
        }

        if (ESPDDocument.getEconomicOperator() != null) {
            EconomicOperatorPartyType economicOperatorPartyType = economicOperatorPartyTypeTransformer
                    .apply(ESPDDocument.getEconomicOperator());
            responseType.getEconomicOperatorParty().add(economicOperatorPartyType);
        }

    }
    
    private void addQualifyingParty(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
    	if(responseType.getEconomicOperatorParty() != null && responseType.getEconomicOperatorParty().size() > 0) {
    		QualifyingPartyType qp=new QualifyingPartyType();
    		if(ESPDDocument.getEoSme() != null) {
    		
    			if(ESPDDocument.getEoSme().getEmployeeQuantity() != null) {    		
    				EmployeeQuantityType eqt=new EmployeeQuantityType();
    				eqt.setValue(ESPDDocument.getEoSme().getEmployeeQuantity());
    				qp.setEmployeeQuantity(eqt);
    			}
    			
    			if(ESPDDocument.getEoSme().getValueAmount() != null) {    	
    				CapabilityType ct=new CapabilityType();
    				ValueAmountType vat=new ValueAmountType();
    				vat.setValue(ESPDDocument.getEoSme().getValueAmount());
    				vat.setCurrencyID(ESPDDocument.getEoSme().getCurrency());
    				ct.setValueAmount(vat);
    				qp.getFinancialCapability().add(ct);
    			}
    		}
    		
    		IndustryClassificationCodeType icct=new IndustryClassificationCodeType();
    		icct.setListID("EOIndustryClassificationCode");
    		icct.setListAgencyID("EU-COM-GROW");
    		icct.setListVersionID("2.1.1");
    		
    		if(ESPDDocument.getEoSme() != null && ESPDDocument.getEoSme().getAnswer() == false) {  
    			icct.setValue("LARGE");
	    		responseType.getEconomicOperatorParty().get(0).getParty().setIndustryClassificationCode(icct);
    		} else {    			
    			if(ESPDDocument.getEconomicOperator().getIndustryClassificationCode() != null) {
    				icct.setValue(ESPDDocument.getEconomicOperator().getIndustryClassificationCode());
    	    		responseType.getEconomicOperatorParty().get(0).getParty().setIndustryClassificationCode(icct);
    			}else {    				
    				icct.setValue("SME");
    				responseType.getEconomicOperatorParty().get(0).getParty().setIndustryClassificationCode(icct);
    			}
    		}
    		
    		
    		responseType.getEconomicOperatorParty().get(0).getQualifyingParty().add(qp);
    	}
    }

    private void addProcurementProjectLots(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
    	if(ESPDDocument.getAuthority() != null) {    	
    		String fiscalCode=ESPDDocument.getAuthority().getVatNumber() == null ? ESPDDocument.getAuthority().getAnotherNationalId() : ESPDDocument.getAuthority().getVatNumber();
    		responseType.getProcurementProjectLot().addAll(CommonUblFactory.buildProcurementProjectLot(ESPDDocument.getLots(),fiscalCode));
    	}else {
    		responseType.getProcurementProjectLot().addAll(CommonUblFactory.buildProcurementProjectLot(ESPDDocument.getLots(),ESPDDocument.getEconomicOperator().getVatNumber()));
    	}
    }
    
    private void addQualificationApplicationTypeCodeInformation(final ESPDDocument espdDocument, final QualificationApplicationResponseType responseType) {
        // TODO scelgo regulated se SA Ã¨ di uno stato diverso dall'Italia?
        // espdDocument.getAuthority().getCountry().getIso2Code()
    	responseType.setQualificationApplicationTypeCode(CommonUblFactory.buildQualificationApplicationTypeCodeType(QualificationApplicationType.EXTENDED.getCode()));
    }

    private void addAdditionalDocumentReference(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
       
        
        if (ESPDDocument.getId() != null && ESPDDocument.getUUID() != null && ESPDDocument.getIssueDate() != null && ESPDDocument.getIssueTime() != null) {
        	responseType.getAdditionalDocumentReference()
            .add(CommonUblFactory.buildEspdRequestReferenceType(ESPDDocument.getId(),ESPDDocument.getUUID(),ESPDDocument.getIssueDate(),ESPDDocument.getIssueTime()));
        }

        // TED_CN
        for (AdditionalDocumentReference adr : ESPDDocument.getAdditionalDocumentReference()) {
			if(ESPDDocument.getId() != null && adr.getNojcnNumber() != null && !adr.getNojcnNumber().equals(ESPDDocument.getId())) {			
				responseType.getAdditionalDocumentReference().add(CommonUblFactory.buildProcurementProcedureType(adr));
			}
		}


    }

    private void addCriteria(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
        /************Commentato momentanemaente********************/
    	boolean alsoResponse = true;
    	List<TenderingCriterionResponseType> tcr = new ArrayList<TenderingCriterionResponseType>();
    	responseType.getTenderingCriterion().addAll(criteriaTransformerRequest.apply(ESPDDocument,responseType,alsoResponse,tcr));
        responseType.getTenderingCriterionResponse().addAll(tcr);
    }

    private void addSignatureInformation(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
       

        NameType nameType = new NameType();
        nameType.setValue(ESPDDocument.getLocation());

        LocationType locationType = new LocationType();
        locationType.setName(nameType);
        
        PartyType signatoryParty = new PartyType();
        signatoryParty.setPhysicalLocation(locationType);

        SignatureType signatureType = new SignatureType();
        signatureType.setSignatoryParty(signatoryParty);
        
        ValidationDateType vdt = new ValidationDateType();
        if(ESPDDocument.getSignatureDate() != null) {        	
        	try {	    	
        		
        		GregorianCalendar cal2 = new GregorianCalendar();	 
        		Date d =  new SimpleDateFormat("yyyy-MM-dd").parse(ESPDDocument.getSignatureDate());
        		cal2.setTime(d);
        		vdt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2));
        		signatureType.setValidationDate(vdt);
        	} catch (DatatypeConfigurationException | ParseException e) {
        		// TODO Auto-generated catch block
        		log.error("Si è verificato un errore nel metodo: addSignatureInformation", e);
        	} 
        }
        IDType idType = CommonUblFactory.buildIdType("#");
        idType.setValue(ESPDDocument.getSignature());
        signatureType.setID(idType);
        

        responseType.getSignature().add(signatureType);
    }
       

    private void addConsortiumName(ESPDDocument ESPDDocument, QualificationApplicationResponseType responseType) {
        if (ESPDDocument.getEoTogetherWithOthers() == null || ESPDDocument.getEoTogetherWithOthers().getDescription3() == null || isBlank(ESPDDocument.getEoTogetherWithOthers().getDescription3())) {
            return;
        }
        
        EconomicOperatorGroupNameType nameType = new EconomicOperatorGroupNameType();
        nameType.setValue(ESPDDocument.getEoTogetherWithOthers().getDescription3());
        responseType.setEconomicOperatorGroupName(nameType);
    }
    
    private boolean isBlank(String s) {
    	if(s == null || "".equals(s)) {
    		return true;
    	}
    	return false;
    }
}
