package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isBlank;
import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isNotBlank;
import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.trimToEmpty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.format.annotation.DateTimeFormat;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AdditionalDocumentReference;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ProcurementProjectLot;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Agency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.DocumentTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ProcedureType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ProfileExecution;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.QualificationApplicationType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.WeightingType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCountry;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.MarshallingConstants;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectLotType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ContractFolderIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CopyIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentDescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.FileNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProcedureCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProcurementTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProfileExecutionIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProfileIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.QualificationApplicationTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UBLVersionIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.URIType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UUIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.VersionIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.WeightScoringMethodologyNoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.WeightingTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Simple factory for creating simple UBL elements that are shared between a {@link QualificationApplicationRequestType}
 * and {@link QualificationApplicationResponseType}.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Slf4j
public final class CommonUblFactory {

    public enum EspdType {
        ESPD_REQUEST,
        ESPD_RESPONSE
    }

    private CommonUblFactory() {

    }

    /**
     * Build an {@link IDType} with the mandatory SchemeAgencyID attribute.
     *
     * @param schemeAgencyID
     * @return A new instance of this element
     */
    public static IDType buildIdType(String schemeAgencyID) {
        IDType idType = new IDType();
        idType.setSchemeAgencyID(schemeAgencyID);
        return idType;
    }

    /**
     *
     * @param value
     * @return
     */
    public static IDType buildIdTypeValue(String value) {
        final IDType idType = new IDType();
        idType.setValue(value);
        return idType;
    }

    /**
     * Indica la versione dello schema UBL 2 utilizzato che definisce tutti gli elementi che potrebbero essere
     * presenti nell'istanza corrente.
     *
     * @return Il corrispondente elemento UBL
     */
    public static UBLVersionIDType buildUblVersionIDType() {
        final UBLVersionIDType versionIDType = new UBLVersionIDType();
        versionIDType.setValue("2.2");
        versionIDType.setSchemeAgencyID("OASIS-UBL-TC");
        return versionIDType;
    }

    /**
     * Identifica la specifica che contiene l'insieme completo di regole riguardanti il contenuto semantico, le
     * cardinalità e le regole a cui sono conformi i dati contenuti nel documento di istanza. L'identificazione
     * può includere la versione della specifica e qualsiasi personalizzazione applicata.
     *
     * @return Il corrispondente elemento UBL
     */
    public static ProfileIDType buiildProfileIDType() {
        final ProfileIDType profileIDType = new ProfileIDType();
        profileIDType.setValue("4.1");
        profileIDType.setSchemeAgencyID("CEN-BII");
        profileIDType.setSchemeVersionID("2.1.1");
        return profileIDType;
    }

    /**
     * Indica la versione dell'Exchange Data Model di ESPD utilizzato per la creazione dell'istanza XML.
     * L'identificativo può includere la versione esatta della specifica.
     *
     * @return Il corrispondente elemento UBL
     */
    public static ProfileExecutionIDType buildProfileExecutionIDType() {
        final ProfileExecution profileExecution = ProfileExecution.ESPD_2_1_1_EXTENDED;
        final ProfileExecutionIDType profileExecutionIDType = new ProfileExecutionIDType();
        profileExecutionIDType.setValue(profileExecution.getCode());
        profileExecutionIDType.setSchemeAgencyID(profileExecution.getListAgencyName());
        return profileExecutionIDType;
    }

    /**
     * Identifica una personalizzazione di UBL definita dall'utente per un uso specifico.
     * ESPD utilizza la versione corrente di ESPD-EDM.
     *
     * @param espdType If it's about a ESPD Request or Response
     * @return Il corrispondente elemento UBL
     */
    
    public static CustomizationIDType buildCustomizationIDTypeRquest(EspdType espdType) {
        final CustomizationIDType customizationIDType = new CustomizationIDType();
        // sembra non servire distinzione tra request e response
        //if (EspdType.ESPD_REQUEST.equals(espdType)) {
            customizationIDType.setValue("urn:www.cenbii.eu:transaction:biitrdm070:ver3.0");
        //} else if (EspdType.ESPD_RESPONSE.equals(espdType)) {
        //    customizationIDType.setValue("urn:www.cenbii.eu:transaction:biitrdm070:ver3.0");
        //}
        customizationIDType.setSchemeAgencyID("CEN-BII");
        customizationIDType.setSchemeVersionID("2.1.1");
        customizationIDType.setSchemeName("CustomizationID");
        return customizationIDType;
    }
    
    public static CustomizationIDType buildCustomizationIDType(EspdType espdType) {
        final CustomizationIDType customizationIDType = new CustomizationIDType();
        // sembra non servire distinzione tra request e response
        //if (EspdType.ESPD_REQUEST.equals(espdType)) {
            customizationIDType.setValue("urn:www.cenbii.eu:transaction:biitrdm092:ver3.0");
        //} else if (EspdType.ESPD_RESPONSE.equals(espdType)) {
        //    customizationIDType.setValue("urn:www.cenbii.eu:transaction:biitrdm070:ver3.0");
        //}
        customizationIDType.setSchemeAgencyID("CEN-BII");
        customizationIDType.setSchemeVersionID("2.1.1");
        customizationIDType.setSchemeName("CustomizationID");
        return customizationIDType;
    }

    /**
     * Identificatore del documento, di norma generato dal sistema che crea il documento ESPD o dall'organizzazione
     * responsabile del documento
     *
     * @param fiscalCode codice fiscale della stazione appaltante
     * @return Il corrispondente elemento UBL
     */
    public static IDType buildDocumentIdentifierType(final String fiscalCode) {
        return buildDocumentIdType(fiscalCode, UUID.randomUUID().toString());
    }
    
    public static IDType buildDocumentIdentifierTypeNew(final String fiscalCode, String id) {
    	if(id != null) {    		
    		return buildDocumentIdType(fiscalCode, id);
    	}
    	return buildDocumentIdType(fiscalCode, UUID.randomUUID().toString());
    }

    /**
     * Indica se il documento è una copia
     *
     * @param isCopy <code>true</code> se è una copia, <code>false</code> altrimenti.
     * @return Il corrispondente elemento UBL
     */
    public static CopyIndicatorType buildCopyIndicatorType(boolean isCopy) {
        CopyIndicatorType copyIndicatorType = new CopyIndicatorType();
        copyIndicatorType.setValue(isCopy);
        return copyIndicatorType;
    }

    /**
     * Identificatore univoco che può essere usato per referenziare l'istanza del
     * documento ESPD
     *
     * @param fiscalCode codice fiscale della stazione appaltante
     * @return Il corrispondente elemento UBL
     */
    public static UUIDType buildUUIDType(final String fiscalCode, String uuid) {
        final UUIDType uuidType = new UUIDType();
        if(uuid != null) {
        	uuidType.setValue(uuid);
        }else {
        	uuidType.setValue(UUID.randomUUID().toString());
        }
       
        uuidType.setSchemeID("ISO/IEC 9834-8:2008");       
        uuidType.setSchemeAgencyName(fiscalCode);
        uuidType.setSchemeAgencyID("#");
        uuidType.setSchemeVersionID("4");              
        return uuidType;
    }


    /**
     * Identificatore specificato dall'acquirente e utilizzato come numero di riferimento per tutti i
     * documenti nel processo di gara. È anche noto come identificativo della procedura di gara, numero di
     * riferimento dell'appalto o identificativo del Fascicolo di Gara. Un riferimento alla procedura di gara
     * a cui sono associati un documento di Qualification Request e i documenti di Response consegnati
     *
     * @param fiscalCode codice fiscale della stazione appaltante
     * @param fileReferenceNumber identificativo della procedura di gara
     * @return Il corrispondente elemento UBL
     */
    public static ContractFolderIDType buildContractFolderType(final String fiscalCode, String fileReferenceNumber) {
        ContractFolderIDType contractFolderIDType = new ContractFolderIDType();
        contractFolderIDType.setValue(fileReferenceNumber);
        contractFolderIDType.setSchemeAgencyID(fiscalCode);
        return contractFolderIDType;
    }


    /**
     * Data in cui il documento è stato rilasciato dalla stazione appaltante
     *
     * @param when The desired date
     * @return Il corrispondente elemento UBL
     */
    public static IssueDateType buildIssueDateType(Instant when) throws DatatypeConfigurationException {
        if (when == null) {
            return null;
        }
        final LocalDate ld = LocalDate.from(when.atZone(ZoneId.systemDefault()));
        final GregorianCalendar c = GregorianCalendar.from(ZonedDateTime.now());
        c.clear();
        c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
        final IssueDateType issueDateType = new IssueDateType();
                
        issueDateType.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
        return issueDateType;
    }

    /**
     * Orario in cui il documento è stato rilasciato dalla stazione appaltante
     *
     * @param when The desired time
     * @return Il corrispondente elemento UBL
     * @throws DatatypeConfigurationException
     */
    public static IssueTimeType buildIssueTimeType(Instant when) throws DatatypeConfigurationException {
        if (when == null) {
            return null;
        }
        final LocalTime lt = LocalTime.from(when.atZone(ZoneId.systemDefault()));
        final GregorianCalendar c = GregorianCalendar.from(ZonedDateTime.now());
        c.clear();
        c.set(0, 0, 0, lt.getHour(), lt.getMinute(), lt.getSecond());
        final IssueTimeType issueTimeType = new IssueTimeType();             
        
        issueTimeType.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
                          
        return issueTimeType;
    }

    /**
     * Versione del contenuto del documento
     *
     * @param version
     * @return Il corrispondente elemento UBL
     */
    public static VersionIDType buildVersionIDType(String version) {
        final VersionIDType versionIDType = new VersionIDType();
        versionIDType.setValue(version);
        versionIDType.setSchemeAgencyID("#");
        return versionIDType;
    }

    /**
     * Tipologia di procedura amministrativa dell'appalto in base alle direttive UE
     *
     * @param procedureCode il codice della procedura definito in allegato B foglio ProcedureType
     * @return Il corrispondente elemento UBL
     */
    public static ProcedureCodeType buildProcedureCodeType(final String procedureCode) {
        final Optional<ProcedureType> optionalProcedureType = ProcedureType.getProcedureTypeByCode(procedureCode);
        if (!optionalProcedureType.isPresent()) {
            return null;
        }
        final ProcedureType procedureType = optionalProcedureType.get();
        final ProcedureCodeType procedureCodeType = new ProcedureCodeType();
        procedureCodeType.setValue(procedureType.getCode());
        procedureCodeType.setListAgencyID(Agency.EU_COM_OP.getIdentifier());
        procedureCodeType.setListID(procedureType.getListId());
        //procedureCodeType.setListAgencyName(procedureType.getListAgencyName());
        procedureCodeType.setListAgencyName("Publications Office of the EU");
        procedureCodeType.setListVersionID(procedureType.getListVersionId());
        return procedureCodeType;
    }
    
    public static ProcurementTypeCodeType buildProjectCodeType(final String projectType) {
            	
        ProcurementTypeCodeType ptct=new ProcurementTypeCodeType();
        ptct.setValue(projectType);
        ptct.setListID("ProjectType");
        ptct.setListAgencyID(Agency.EU_COM_OP.getIdentifier());
        ptct.setListVersionID("1.0");      
      
        return ptct;
    }

    /**
     * Codice che specifica il tipo di ualificazione utilizzata
     *
     * @param qualificationApplicationTypeCode
     * @return Il corrispondente elemento UBL
     */
    public static QualificationApplicationTypeCodeType buildQualificationApplicationTypeCodeType(final String qualificationApplicationTypeCode) {
        final Optional<QualificationApplicationType> optionalQualificationApplicationType = QualificationApplicationType.getQualificationApplicationTypeByCode(qualificationApplicationTypeCode);
        if (!optionalQualificationApplicationType.isPresent()) {
            throw new IllegalArgumentException("Param " + qualificationApplicationTypeCode + " not valid");
        }
        final QualificationApplicationType qualificationApplicationType = optionalQualificationApplicationType.get();
        final QualificationApplicationTypeCodeType qualificationApplicationTypeCodeType = new QualificationApplicationTypeCodeType();
        qualificationApplicationTypeCodeType.setValue(qualificationApplicationType.getCode());
        qualificationApplicationTypeCodeType.setListID(qualificationApplicationType.getListId());
        qualificationApplicationTypeCodeType.setListAgencyID(Agency.EU_COM_GROW.getIdentifier());
        qualificationApplicationTypeCodeType.setListAgencyName(qualificationApplicationType.getListAgencyName());
        qualificationApplicationTypeCodeType.setListAgencyName(Agency.EU_COM_GROW.getLongName());        
        qualificationApplicationTypeCodeType.setListVersionID(qualificationApplicationType.getListVersionId());
        return qualificationApplicationTypeCodeType;
    }

    /**
     * Testo libero per descrivere la metodologia di punteggio
     *
     * @param weightScoringMethodologyNotes lista delle descrizioni.
     * @return Il corrispondente elemento UBL
     */
    public static Collection<WeightScoringMethodologyNoteType> buildWeightScoringMethodologyNotesType(final List<String> weightScoringMethodologyNotes) {
        return weightScoringMethodologyNotes.stream()
                .map(definitions ->  {
                    final WeightScoringMethodologyNoteType weightScoringMethodologyNoteType = new WeightScoringMethodologyNoteType();
                    weightScoringMethodologyNoteType.setValue(definitions);
                    return weightScoringMethodologyNoteType;
                })
                .collect(Collectors.toList());
    }

    /**
     * Codice che specifica il tipo di ponderazione
     *
     * @param weightingTypeCode il codice del tipo di ponderazione
     * @return Il corrispondente elemento UBL
     */
    public static WeightingTypeCodeType buildWeightingTypeCodeType(final String weightingTypeCode) {
        if (isBlank(weightingTypeCode)) {
            return null;
        }
        final Optional<WeightingType> optionalWeightingType = WeightingType.getWeightingTypeByCode(weightingTypeCode);
        if (!optionalWeightingType.isPresent()) {
            return null;
        }
        final WeightingType weightingType = optionalWeightingType.get();
        final WeightingTypeCodeType weightingTypeCodeType = new WeightingTypeCodeType();
        weightingTypeCodeType.setValue(weightingType.getCode());
        weightingTypeCodeType.setListID(weightingType.getListId());
        weightingTypeCodeType.setListVersionID(weightingType.getListVersionId());
        weightingTypeCodeType.setListAgencyName(weightingType.getListAgencyName());
        return weightingTypeCodeType;
    }

    /**
     * Reference to the Contract Notice in TeD.
     * <p></p>
     * For procurement projects above the threshold it is compulsory to specify the following data,
     * by means of an AdditionalDocumentReference element, about the Contract Notice published in TeD:
     * the OJEU S number[], date[], page[], Notice number in OJS: YYYY/S [][][]-[][][][][][],
     * Title and Description of the Procurement Project
     *
     * @param espdDocument The ESPD model containing the contract notice information
     *
     * @return A UBL document reference element
     */
    public static DocumentReferenceType buildProcurementProcedureType(AdditionalDocumentReference adr) {
        final DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        final IDType idType = new IDType();
        // TODO verificare i valori dei classici attributi IDType che non qui specificati
        if (isBlank(adr.getNojcnNumber())) {
            idType.setValue(MarshallingConstants.TEMPORARY_OJS_NUMBER);
            // documentReferenceType.setID(buildTemporaryDocumentIdType(MarshallingConstants.TEMPORARY_OJS_NUMBER));
        } else {
            idType.setValue(adr.getNojcnNumber());
        }
        idType.setSchemeAgencyID("#");
        documentReferenceType.setID(idType);
        final Instant now = Instant.now();
        try {
			documentReferenceType.setIssueTime(CommonUblFactory.buildIssueTimeType(now));
			documentReferenceType.setIssueDate(CommonUblFactory.buildIssueDateType(now));
		} catch (DatatypeConfigurationException e) {
			log.error("Error during set issue date or time in document reference (buildProcurementProcedureType)", e);
			
		}
       

        // A reference to a Contract Notice published in the TeD platform (European Commission, Office of Publications).
        
        if(adr.getDocTypeCode() != null) {        
        	documentReferenceType.setDocumentTypeCode(buildDocumentTypeCode(DocumentTypeCode.valueOf(adr.getDocTypeCode())));
        }

             
       documentReferenceType.setAttachment(buildAttachmentType(adr.getTedUrl(), adr.getTitle(),adr.getDescr(), adr.getTedReceptionId()));
        

        return documentReferenceType;
    }

   

    /**
     * Build a reference to the original {@link QualificationApplicationRequestType} document that was used to generate a
     * {@link QualificationApplicationResponseType}.
     *
     * @param metadata Information regarding the ESPD request
     *
     * @return A UBL document reference element
     */
    public static DocumentReferenceType buildEspdRequestReferenceType(String id,String uuid,String issueDate,String issueTime) {
        

        final DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        IDType idType=buildIdTypeValue(id);
        idType.setSchemeAgencyID("#");
        documentReferenceType.setID(idType);
        UUIDType uuidType=new UUIDType();
        uuidType.setValue(uuid);
        uuidType.setSchemeAgencyID("#");
        documentReferenceType.setUUID(uuidType);
        try {
        	IssueTimeType it=new IssueTimeType();
        	GregorianCalendar cal = new GregorianCalendar();
        	Date date1=new SimpleDateFormat("hh:mm:ss").parse(issueTime);  
		    cal.setTime(date1);
        	it.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            documentReferenceType.setIssueTime(it);
            
            IssueDateType iDate=new IssueDateType();
            GregorianCalendar cal2 = new GregorianCalendar();
            Date date2 =  new SimpleDateFormat("yyyy-MM-dd").parse(issueDate);        	
        	cal2.setTime(date2);
        	iDate.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal2));
            documentReferenceType.setIssueDate(iDate);
        } catch (DatatypeConfigurationException dce) {
            log.error("Error during set issue date or time in document reference", dce);
        } catch (ParseException e) {
        	log.error("Error during set issue date or time in document reference", e);
		}
        documentReferenceType.setDocumentTypeCode(buildDocumentTypeCode(DocumentTypeCode.ESPD_REQUEST));
        //documentReferenceType.getDocumentDescription().add(buildDocumentDescription(metadata.getDescription()));
        //documentReferenceType.setAttachment(buildAttachmentType(metadata.getUrl(), null, null, null));

        return documentReferenceType;
    }       

    private static DocumentTypeCodeType buildDocumentTypeCode(DocumentTypeCode typeCode) {
        final DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
        documentTypeCode.setListAgencyID("#");
        documentTypeCode.setListAgencyName(typeCode.getListAgencyName());
        documentTypeCode.setListID(typeCode.getListId());
        documentTypeCode.setListVersionID(typeCode.getListVersionId());
        documentTypeCode.setValue(typeCode.getCode());
        return documentTypeCode;
    }
    
    private static DocumentDescriptionType buildDocumentDescription(String description) {
        DocumentDescriptionType descriptionType = new DocumentDescriptionType();
        descriptionType.setValue(description);
        return descriptionType;
    }

    private static AttachmentType buildAttachmentType(String url, String fileName, String description,
            String tedReceptionId) {
        final AttachmentType attachmentType = new AttachmentType();
        final ExternalReferenceType externalReferenceType = new ExternalReferenceType();

        if (isNotBlank(url)) {
            final URIType uriType = new URIType();
            uriType.setValue(url);
            externalReferenceType.setURI(uriType);
        }

        if (isNotBlank(fileName)) {
            final FileNameType fileNameType = new FileNameType();
            fileNameType.setValue(fileName);
            externalReferenceType.setFileName(fileNameType);
        }

        final DescriptionType descriptionType = new DescriptionType();
        if (isNotBlank(description)) {
            descriptionType.setValue(description);
        } else {
            // we need a default value so that we can read the tedReceptionId as a second description
            descriptionType.setValue("-");
        }
        externalReferenceType.getDescription().add(descriptionType);

        if (isNotBlank(tedReceptionId)) {
            DescriptionType receptionDescription = new DescriptionType();
            receptionDescription.setValue(tedReceptionId);
            externalReferenceType.getDescription().add(receptionDescription);
        }

        attachmentType.setExternalReference(externalReferenceType);

        return attachmentType;
    }

//    public static Collection<ProcurementProjectLotType> buildProcurementProjectLot(final List<ProcurementProjectLot> lots, final String authorityFiscalCode) {
//        if (lots == null || lots.size() == 0) {
//            throw new IllegalArgumentException("Field lots must be not null or empty");
//        }
//        return lots.stream()
//                .map(lot -> {
//                    final ProcurementProjectLotType lotType = new ProcurementProjectLotType();
//                    final IDType idType = buildIdType(authorityFiscalCode);
//                    idType.setValue(String.format("%s_%s", trimToEmpty(lot.getNumLot()), trimToEmpty(lot.getCigLot())));
//                    lotType.setID(idType);
//                    return lotType;
//                })
//                .collect(Collectors.toList());
//    }
    
    public static Collection<ProcurementProjectLotType> buildProcurementProjectLot(final List<ProcurementProjectLot> lots, final String authorityFiscalCode) {
        if (lots == null || lots.size() == 0) {
            throw new IllegalArgumentException("Field lots must be not null or empty");
        }
        return lots.stream()
                .map(lot -> {
                    final ProcurementProjectLotType lotType = new ProcurementProjectLotType();
                    final IDType idType = buildIdType(authorityFiscalCode);
                    idType.setValue(trimToEmpty(lot.getNumLot()));
                    lotType.setID(idType);
                    return lotType;
                })
                .collect(Collectors.toList());
    }

    static CountryType buildCountryType(CacCountry country) {
        final CountryType countryType = new CountryType();
        final IdentificationCodeType identificationCodeType = new IdentificationCodeType();
        identificationCodeType.setValue(country.getIso2Code());
        identificationCodeType.setListID(country.getListId());
        identificationCodeType.setListAgencyName(country.getListAgencyName());
        identificationCodeType.setListAgencyID("ISO");
        identificationCodeType.setListVersionID(country.getListVersionId());
        countryType.setIdentificationCode(identificationCodeType);

        final NameType nameType = new NameType();
        nameType.setValue(country.getName());
        countryType.setName(nameType);

        return countryType;
    }

    private static IDType buildDocumentIdType(final String fiscalCode, final String id) {
        return buildDocumentIdType(fiscalCode, id, "ISO/IEC 9834-8:2014");
    }

    private static IDType buildTemporaryDocumentIdType(String id) {
        return buildDocumentIdType("", id, MarshallingConstants.TEMPORARY_OJS_NUMBER_SCHEME_ID);
    }

    private static IDType buildDocumentIdType(String fiscalCode, String value, String schemeId) {
        IDType idType = buildIdType(fiscalCode);
        idType.setSchemeID(schemeId);
        // TODO contracting authority name??
        //idType.setSchemeAgencyName(Agency.EU_COM_GROW.getLongName());
        idType.setSchemeVersionID("4");
        idType.setValue(value);

        return idType;
    }
}
