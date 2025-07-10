package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.infrastructure.CriterionDefinitions;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

@Component
@Slf4j
public class CriteriaToEspdDocumentPopulator {

	@SuppressWarnings("java:S3749")
	private final EspdResponseCriterionFactory criterionFactory;

	public CriteriaToEspdDocumentPopulator() {
		this(new EspdResponseCriterionFactory());
	}

	public CriteriaToEspdDocumentPopulator(EspdResponseCriterionFactory criterionFactory) {
		this.criterionFactory = criterionFactory;
	}

    /**
     * Update criteria information on the given ESPD document.
     * <p>
     * <b>
     * Please be aware that this method mutates the ESPD document!
     * </b>
     * </p>
     *
     * @param espdDocument The given ESPD document to be updated with criteria information
     * @param ublCriteria  UBL criteria from which we read the information
     */
    public void addCriteriaToEspdDocument(ESPDDocument espdDocument, List<TenderingCriterionType> ublCriteria, QualificationApplicationRequestType requestType, QualificationApplicationResponseType responseType) {
        if (CollectionUtils.isEmpty(ublCriteria)) {
            return;
        }

        for (TenderingCriterionType ublCriterion : ublCriteria) {
            setCriterionValueOnEspdModel(espdDocument, ublCriterion,requestType, responseType);
        }
        
        //espdDocument.setUblCriteria(ublCriteria);
    }

    private void setCriterionValueOnEspdModel(ESPDDocument espdDocument, TenderingCriterionType ublCriterion,QualificationApplicationRequestType requestType,
    		QualificationApplicationResponseType responseType) {
    	//System.out.println("CRITERION UUID: "+ublCriterion.getID().getValue());
        Optional<CacCriterion> ccvCriterion = CriterionDefinitions.findCriterionById(ublCriterion.getID().getValue());
        //if(ccvCriterion != null && ccvCriterion.get() != null) {
        	
        //}
        	
        if (!ccvCriterion.isPresent()) {
            return;
        }
        try {
        	if(requestType != null) {        	
        		PropertyUtils.setProperty(espdDocument, ccvCriterion.get().getEspdDocumentField(),
        				criterionFactory.buildEspdCriterion(ccvCriterion.get(), ublCriterion));
        	}else if(responseType != null){
        		List<TenderingCriterionResponseType> questions=null;
        		questions=responseType.getTenderingCriterionResponse();
        		List<EvidenceType> evidences=responseType.getEvidence();
        		PropertyUtils.setProperty(espdDocument, ccvCriterion.get().getEspdDocumentField(),
        				criterionFactory.buildEspdCriterionFromResponse(ccvCriterion.get(), ublCriterion,questions,evidences));
        	}
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
    }

}
