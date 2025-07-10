package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting;

import static org.springframework.util.CollectionUtils.isEmpty;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.QualificationApplicationType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.util.EspdConfiguration;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.CommonUblFactory;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UBLAdditionalDocumentReferenceTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblContractingPartyTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblProcurementProjectTypeTransformer;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ItemClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;

import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.apache.tomcat.util.digester.SetPropertiesRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Transforms a {@link ESPDDocument} into a {@link QualificationApplicationRequestType}.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Component
@Slf4j
public class UblRequestTypeTransformer {

    private final UblContractingPartyTypeTransformer contractingPartyTransformer;
    private final UblProcurementProjectTypeTransformer procurementProjectTypeTransformer;
    private final UBLAdditionalDocumentReferenceTypeTransformer documentReferenceTypeTransformer;
    private final UblRequestCriteriaTransformer criteriaTransformer;
    private final EspdConfiguration espdConfiguration;

    /**
     *
     * @param contractingPartyTransformer
     * @param procurementProjectTypeTransformer
     * @param documentReferenceTypeTransformer
     * @param criteriaTransformer
     * @param espdConfiguration
     */
    @Autowired
    UblRequestTypeTransformer(UblContractingPartyTypeTransformer contractingPartyTransformer,
                              UblProcurementProjectTypeTransformer procurementProjectTypeTransformer,
                              UBLAdditionalDocumentReferenceTypeTransformer documentReferenceTypeTransformer,
                              UblRequestCriteriaTransformer criteriaTransformer,
                              EspdConfiguration espdConfiguration) {
        this.contractingPartyTransformer = contractingPartyTransformer;
        this.procurementProjectTypeTransformer = procurementProjectTypeTransformer;
        this.documentReferenceTypeTransformer = documentReferenceTypeTransformer;
        this.criteriaTransformer = criteriaTransformer;
        this.espdConfiguration = espdConfiguration;
    }

    public QualificationApplicationRequestType buildRequestType(ESPDDocument espdDocument) {
        QualificationApplicationRequestType espdRequestType = new QualificationApplicationRequestType();

        // Process Control
        addUBLVersionInformation(espdRequestType);
        addProfileIdInformation(espdRequestType);
        addProfileExecutionInformation(espdRequestType);
        addCustomizationInformation(espdRequestType);

        // Root elements
        addIdInformation(espdDocument, espdRequestType);
        addCopyIndicatorInformation(espdRequestType);
        addUUIDInformation(espdDocument, espdRequestType);
        // TODO verificare il campo
        addContractFolderIdInformation(espdDocument, espdRequestType);
        addIssueDateAndTimeInformation(espdDocument,espdRequestType);
        addVersionIdInformation(espdRequestType);
        addPreviousVersionInformation(espdRequestType);        
        addProcedureCodeInformation(espdDocument, espdRequestType);
        
        addQualificationApplicationTypeCodeInformation(espdDocument, espdRequestType);
        addWeightScoringMethodologyInformation(espdDocument, espdRequestType);
        addWeightingTypeInformation(espdDocument, espdRequestType);

        // Contracting party
        addContractingPartyInformation(espdDocument, espdRequestType);

        // Procedura di gara
        	//addProcurementProjectInformation(espdDocument, espdRequestType);

        // Lotti procedura di gara
        addProcurementProjectLots(espdDocument, espdRequestType);
        addProcurementProject(espdDocument, espdRequestType);
        // Criteri
        addCriteria(espdDocument, espdRequestType);

        addAdditionalDocumentReference(espdDocument, espdRequestType);

        return espdRequestType;
    }

    private void addUBLVersionInformation(final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setUBLVersionID(CommonUblFactory.buildUblVersionIDType());
    }

    private void addProfileIdInformation(final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setProfileID(CommonUblFactory.buiildProfileIDType());
    }

    private void addProfileExecutionInformation(final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setProfileExecutionID(CommonUblFactory.buildProfileExecutionIDType());
    }

    private void addCustomizationInformation(final QualificationApplicationRequestType espdRequestType) {
        espdRequestType
                .setCustomizationID(CommonUblFactory.buildCustomizationIDTypeRquest(CommonUblFactory.EspdType.ESPD_REQUEST));
    }

    private void addIdInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setID(CommonUblFactory.buildDocumentIdentifierTypeNew(espdDocument.getAuthority().getVatNumber(),espdDocument.getId()));
    }

    private void addCopyIndicatorInformation(final QualificationApplicationRequestType espdRequestType) {

        espdRequestType.setCopyIndicator(CommonUblFactory.buildCopyIndicatorType(false));
    }

    private void addUUIDInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setUUID(CommonUblFactory.buildUUIDType(espdDocument.getAuthority().getVatNumber(),espdDocument.getUUID()));
    }

    private void addContractFolderIdInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
        espdRequestType.setContractFolderID(CommonUblFactory.buildContractFolderType(espdDocument.getAuthority().getVatNumber(), espdDocument.getFileRefByCA()));
    }

    private void addIssueDateAndTimeInformation(final ESPDDocument espdDocument,final QualificationApplicationRequestType espdRequestType) {
        final Instant now = Instant.now();
        try {
        	if(espdDocument.getIssueDate() != null) {
        		
	        	IssueDateType iDate=new IssueDateType();
	        	GregorianCalendar cal2 = new GregorianCalendar();
	        	Date date2 =  new SimpleDateFormat("yyyy-MM-dd").parse(espdDocument.getIssueDate());
	        	//Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(ESPDDocument.getIssueDate()); 
	        	cal2.setTime(date2);
	        	iDate.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2));
	        	espdRequestType.setIssueDate(iDate);
        	} else {
        		espdRequestType.setIssueDate(CommonUblFactory.buildIssueDateType(now));
        	}
        	
        	if(espdDocument.getIssueTime() != null) {
        		IssueTimeType it=new IssueTimeType();
	        	GregorianCalendar cal = new GregorianCalendar();
	        	Date date1;				
				date1 = new SimpleDateFormat("hh:mm:ss").parse(espdDocument.getIssueTime());				 
	        	cal.setTime(date1);
	        	it.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));	        	
        		espdRequestType.setIssueTime(it);
        	} else {
        		espdRequestType.setIssueTime(CommonUblFactory.buildIssueTimeType(now));
        	}
           
            
        } catch (DatatypeConfigurationException | ParseException e) {
            log.error("Exception during set issue time or issue date", e);
        }
    }

    private void addVersionIdInformation(final QualificationApplicationRequestType espdRequestType) {
        // TODO gestire versionamento del documento?
        espdRequestType.setVersionID(CommonUblFactory.buildVersionIDType("1.0"));
    }

    private void addPreviousVersionInformation(final QualificationApplicationRequestType espdRequestType) {
        // TODO se gestione versionamento del documento allora mettere la versione precedente
    }

    private void addProcedureCodeInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
    	
    	espdRequestType.setProcedureCode(CommonUblFactory.buildProcedureCodeType(espdDocument.getProcedureCode()));
    }
    
    private void addProcurementProject(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
    	ProcurementProjectType ppt=new ProcurementProjectType();
    	if(espdDocument.getCpvs() != null) {    	
    		for (String cpv : espdDocument.getCpvs()) {
    			CommodityClassificationType cct=new CommodityClassificationType();
    			ItemClassificationCodeType ict=new ItemClassificationCodeType();
    			ict.setValue(cpv);
    			cct.setItemClassificationCode(ict);
    			ppt.getMainCommodityClassification().add(cct);
    		}
    	}
    	
    	
    	
    	
    	
    	espdRequestType.setProcurementProject(ppt);
    	NameType nt=new NameType();
    	nt.setValue(espdDocument.getProcedureTitle());
    	espdRequestType.getProcurementProject().getName().add(nt);
    	DescriptionType dt=new DescriptionType();
    	dt.setValue(espdDocument.getProcedureShortDesc());
    	espdRequestType.getProcurementProject().getDescription().add(dt);
    	
    	IDType id = new IDType();
    	if(espdDocument.getCodiceANAC() != null) {  
        	id.setSchemeAgencyID("EU-COM-GROW");
        	id.setSchemeVersionID("2.1.1");
        	id.setValue(espdDocument.getCodiceANAC());
        	espdRequestType.getProcurementProject().setID(id);
    	}
    	
    	if(espdDocument.getProjectType() != null) {    	
    		espdRequestType.getProcurementProject().setProcurementTypeCode(CommonUblFactory.buildProjectCodeType(espdDocument.getProjectType()));
    	}
    	
    	
    	
    	
    	
    }

    private void addQualificationApplicationTypeCodeInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
        // TODO scelgo regulated se SA è di uno stato diverso dall'Italia?
        // espdDocument.getAuthority().getCountry().getIso2Code()
        espdRequestType.setQualificationApplicationTypeCode(CommonUblFactory.buildQualificationApplicationTypeCodeType(QualificationApplicationType.EXTENDED.getCode()));
    }

    private void addWeightScoringMethodologyInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {
        if (isEmpty(espdDocument.getWeightScoringMethodologyNotes())) {
            return;
        }
        espdRequestType.getWeightScoringMethodologyNote().addAll(CommonUblFactory.buildWeightScoringMethodologyNotesType(espdDocument.getWeightScoringMethodologyNotes()));
    }

    private void addWeightingTypeInformation(final ESPDDocument espdDocument, final QualificationApplicationRequestType espdRequestType) {

        espdRequestType.setWeightingTypeCode(CommonUblFactory.buildWeightingTypeCodeType(espdDocument.getWeightingScoringType()));
    }

    private void addContractingPartyInformation(ESPDDocument espdDocument, QualificationApplicationRequestType espdRequestType) {
        final ContractingPartyType contractingPartyType = contractingPartyTransformer.apply(espdDocument.getAuthority());
        espdRequestType.getContractingParty().add(contractingPartyType);
    }

    /**
     * Utilizzato per identificare la procedura di gara
     *
     * @param espdDocument
     * @param espdRequestType
     */
    private void addProcurementProjectInformation(ESPDDocument espdDocument, QualificationApplicationRequestType espdRequestType) {
        final ProcurementProjectType procurementProjectType = procurementProjectTypeTransformer.apply(espdDocument.getProcurementProject());
        espdRequestType.setProcurementProject(procurementProjectType);
    }

    /**
     * Sotto-progetto in cui può essere suddivisa una procedura di gara. Un lotto presenta
     * caratteristiche specifiche (es. obiettivi differenti e differenti criteri di selezione)
     * e solitamente genera il proprio contratto
     *
     * @param espdDocument
     * @param espdRequestType
     */
    private void addProcurementProjectLots(ESPDDocument espdDocument, QualificationApplicationRequestType espdRequestType) {
        espdRequestType.getProcurementProjectLot()
                       .addAll(CommonUblFactory.buildProcurementProjectLot(espdDocument.getLots(), espdDocument.getAuthority().getVatNumber()));
    }

    private void addAdditionalDocumentReference(ESPDDocument espdDocument, QualificationApplicationRequestType espdRequestType) {
        final Collection<DocumentReferenceType> documents = documentReferenceTypeTransformer.apply(espdDocument);
        espdRequestType.getAdditionalDocumentReference().addAll(documents);
    }

    private void addCriteria(ESPDDocument espdDocument, QualificationApplicationRequestType espdRequestType) {
        espdRequestType.getTenderingCriterion().addAll(criteriaTransformer.apply(espdDocument,null,false,null));
    }

}