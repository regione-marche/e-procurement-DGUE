package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml;

import java.io.InputStream;
import java.math.BigDecimal;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDInfo;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.OtherOe;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblResponseImporter;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

@Slf4j
@Component
public class EspdXmlInfo {
	
	private final Jaxb2Marshaller jaxb2Marshaller;
	private final UblResponseImporter responseToESPDDocumentTransformer;
	
	@Autowired
	EspdXmlInfo(Jaxb2Marshaller jaxb2Marshaller, UblResponseImporter responseToESPDDocumentTransformer) {
		this.jaxb2Marshaller = jaxb2Marshaller;	
		this.responseToESPDDocumentTransformer = responseToESPDDocumentTransformer;
	}
	
	@SuppressWarnings("unchecked")
	public ESPDInfo xmlInfo(InputStream espdResponseStream, String nameFile) {
							
		ESPDInfo info = new ESPDInfo();
		boolean owner = false;
		String id = null;
		String espdName = nameFile;
		String economicOperatorName = null;		
		String economicOperatorCode = null;
		String economicOperatorCF = null;
		String economicOperatorRole = null;
		String RTIGroupName = null;
		String RTIPartName = null;
		String consorzioInfo = null;
		Boolean isConsorzio = false;
		Boolean isGruppo = false;
		boolean exclusionCriterion = false;
		try {
			JAXBElement<QualificationApplicationResponseType> element = (JAXBElement<QualificationApplicationResponseType>) jaxb2Marshaller
					.unmarshal(new StreamSource(espdResponseStream));
			QualificationApplicationResponseType responseType = element.getValue();
			Optional<ESPDDocument> espd=Optional.of(responseToESPDDocumentTransformer.importResponse(responseType));
			
			info.setEspdName(espdName);
			
			if(APIConstants.OWNER_CODE.equals(responseType.getID().getSchemeName())) {
				owner = true;
			}
			info.setOwner(owner);
			
			id = responseType.getID().getValue();
			info.setId(id);
			
			economicOperatorName = responseType.getEconomicOperatorParty().get(0).getParty().getPartyName().get(0).getName().getValue();
			info.setEconomicOperatorName(economicOperatorName);
						
			economicOperatorCode = responseType.getEconomicOperatorParty().get(0).getParty().getPartyIdentification().get(0).getID().getValue();
			info.setEconomicOperatorCode(economicOperatorCode);
			
			economicOperatorRole = responseType.getEconomicOperatorParty().get(0).getEconomicOperatorRole().getRoleCode().getValue();
			info.setEconomicOperatorRole(economicOperatorRole);
			
			exclusionCriterion = existsExsclusionCriteria(espd.get());
			info.setExclusionCriterion(exclusionCriterion);
			
			isConsorzio = espd.get().getEoTogetherWithOthers().getAnswer2();
			info.setIsConsorzio(isConsorzio);
			
			consorzioInfo = espd.get().getEoTogetherWithOthers().getDescription4();
			info.setSetConsorzioInfo(consorzioInfo);
			
			isGruppo = espd.get().getEoTogetherWithOthers().getAnswer();
			info.setIsGruppo(isGruppo);
			
			RTIGroupName = espd.get().getEoTogetherWithOthers().getDescription3();			
			info.setRTIGroupName(RTIGroupName);
			
			RTIPartName = espd.get().getEoTogetherWithOthers().getDescription2();
			info.setRTIPartName(RTIPartName);
			
			economicOperatorCF = espd.get().getEconomicOperator().getAnotherNationalId();
			info.setEconomicOperatorCF(economicOperatorCF);
			
			List<OtherOe> otherOe=new ArrayList<OtherOe>();
			
			if(espd.get().getReliedEntities() != null && 
			   espd.get().getReliedEntities().getUnboundedGroups() != null &&
			   espd.get().getReliedEntities().getUnboundedGroups().size() > 0) {
				for (DynamicRequirementGroup d : espd.get().getReliedEntities().getUnboundedGroups()) {
					if(d != null && !d.isEmpty()) {
						OtherOe oe=new OtherOe();
						oe.setName((String)d.get("description1"));
						oe.setIdentifier((String)d.get("description2"));
						oe.setRole("2");
						oe.setActivity((String)d.get("description3"));
						otherOe.add(oe);
					}
					
				}
			}
			
			if(espd.get().getNotReliedEntities() != null && 
			   espd.get().getNotReliedEntities().getUnboundedGroups() != null &&
			   espd.get().getNotReliedEntities().getUnboundedGroups().size() > 0) {
				for (DynamicRequirementGroup d : espd.get().getNotReliedEntities().getUnboundedGroups()) {
					if(d != null && !d.isEmpty()) {
						OtherOe oe=new OtherOe();
						//oe.setName((String)d.get("description1"));
						//oe.setIdentifier((String)d.get("description2"));
						oe.setActivity((String)d.get("description3"));
						//oe.setPerformance((String)d.get("description4"));
						BigDecimal quote = (BigDecimal)d.get("description5");
						if(quote != null) {
							oe.setQuote(quote.toString());
						}					
						oe.setRole("1");
						otherOe.add(oe);
					}					
				}
			}
			
			info.setOtherOe(otherOe);
			
			return info;			
		}catch (Exception e) {
			log.error("Errore durante il metodo xmlInfo", e);
			throw e;			
		}
	}
	
	private boolean existsExsclusionCriteria(ESPDDocument espd) {
		
		if(espd.getCriminalConvictions() != null && espd.getCriminalConvictions().getAnswer() != null && espd.getCriminalConvictions().getAnswer() == true){
		    return true;
		}
		if(espd.getCorruption() != null && espd.getCorruption().getAnswer() != null && espd.getCorruption().getAnswer() == true){
		    return true;
		}
		if(espd.getFraud() != null && espd.getFraud().getAnswer() != null && espd.getFraud().getAnswer() == true){
		    return true;
		}
		if(espd.getTerroristOffences() != null && espd.getTerroristOffences().getAnswer() != null && espd.getTerroristOffences ().getAnswer() == true){
		    return true;
		}
		if(espd.getMoneyLaundering() != null && espd.getMoneyLaundering().getAnswer() != null && espd.getMoneyLaundering().getAnswer() == true){
		    return true;
		}
		if(espd.getChildLabour() != null && espd.getChildLabour().getAnswer() != null && espd.getChildLabour().getAnswer() == true){
		    return true;
		}
		if(espd.getPaymentTaxes() != null && espd.getPaymentTaxes().getAnswer() != null && espd.getPaymentTaxes().getAnswer() == true){
		    return true;
		}
		if(espd.getPaymentSocialSecurity() != null && espd.getPaymentSocialSecurity().getAnswer() != null && espd.getPaymentSocialSecurity().getAnswer() == true){
		    return true;
		}
		if(espd.getBreachingObligationsEnvironmental() != null && espd.getBreachingObligationsEnvironmental().getAnswer() != null && espd.getBreachingObligationsEnvironmental().getAnswer() == true){
		    return true;
		}
		if(espd.getBreachingObligationsSocial() != null && espd.getBreachingObligationsSocial().getAnswer() != null && espd.getBreachingObligationsSocial().getAnswer() == true){
		    return true;
		}
		if(espd.getBreachingObligationsLabour() != null && espd.getBreachingObligationsLabour().getAnswer() != null && espd.getBreachingObligationsLabour().getAnswer() == true){
		    return true;
		}
		if(espd.getBankruptcy() != null && espd.getBankruptcy().getAnswer() != null && espd.getBankruptcy().getAnswer() == true){
		    return true;
		}
		if(espd.getInsolvency() != null && espd.getInsolvency().getAnswer() != null && espd.getInsolvency().getAnswer() == true){
		    return true;
		}
		if(espd.getArrangementWithCreditors() != null && espd.getArrangementWithCreditors().getAnswer() != null && espd.getArrangementWithCreditors().getAnswer() == true){
		    return true;
		}		
		if(espd.getGuiltyGrave() != null && espd.getGuiltyGrave().getAnswer() != null && espd.getGuiltyGrave().getAnswer() == true){
		    return true;
		}
		if(espd.getAgreementsWithOtherEO() != null && espd.getAgreementsWithOtherEO().getAnswer() != null && espd.getAgreementsWithOtherEO().getAnswer() == true){
		    return true;
		}
		if(espd.getConflictInterest() != null && espd.getConflictInterest().getAnswer() != null && espd.getConflictInterest().getAnswer() == true){
		    return true;
		}
		if(espd.getInvolvementPreparationProcurement() != null && espd.getInvolvementPreparationProcurement().getAnswer() != null && espd.getInvolvementPreparationProcurement().getAnswer() == true){
		    return true;
		}
		if(espd.getEarlyTermination() != null && espd.getEarlyTermination().getAnswer() != null && espd.getEarlyTermination().getAnswer() == true){
		    return true;
		}
		if(espd.getGuiltyMisinterpretation() != null && espd.getGuiltyMisinterpretation().getAnswer() != null && espd.getGuiltyMisinterpretation().getAnswer() == false){
		    return true;
		}
		if(espd.getNationalExclusionGrounds() != null && espd.getNationalExclusionGrounds().getAnswer() != null && espd.getNationalExclusionGrounds().getAnswer() == true){
		    return true;
		}
		return false;
	}
	
	
	private static long dateDifference(Date d1, Date d2) {
		return (d1.getTime() - d2.getTime()) / 86400000L;
	}
}
