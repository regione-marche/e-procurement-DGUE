package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.importing.UblRequestImporter;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblRequestResponseMerger;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblResponseImporter;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Class used to import XML files containing ESPD Requests or Responses.
 * <p>
 * Created by ratoico on 1/23/17.
 */
@Slf4j
@Component
public class EspdXmlImporter {

	private final Jaxb2Marshaller jaxb2Marshaller;
	private final UblRequestImporter requestToESPDDocumentTransformer;
	private final UblResponseImporter responseToESPDDocumentTransformer;
	private final UblRequestResponseMerger requestResponseMerger;

	@Autowired
	EspdXmlImporter(Jaxb2Marshaller jaxb2Marshaller, UblRequestImporter requestToESPDDocumentTransformer,
			UblResponseImporter responseToESPDDocumentTransformer, UblRequestResponseMerger requestResponseMerger) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.requestToESPDDocumentTransformer = requestToESPDDocumentTransformer;
		this.responseToESPDDocumentTransformer = responseToESPDDocumentTransformer;
		this.requestResponseMerger = requestResponseMerger;
	}

	/**
	 * Convert a {@link QualificationApplicationRequestType} coming from an input stream into a {@link ESPDDocument} object needed by
	 * the web application user interface.
	 *
	 * @param espdRequestStream An input stream containing the ESPD Request
	 *
	 * @return An {@link ESPDDocument} object coming out from the stream if it contained a valid ESPD Request
	 * wrapped in an {@link Optional} or an empty {@link Optional} if the import was unsuccessful.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ESPDDocument> importEspdRequest(InputStream espdRequestStream) {
		try {
			JAXBElement<QualificationApplicationRequestType> element = (JAXBElement<QualificationApplicationRequestType>) jaxb2Marshaller
					.unmarshal(new StreamSource(espdRequestStream));
			QualificationApplicationRequestType requestType = element.getValue();
			Optional<ESPDDocument> espd=Optional.of(requestToESPDDocumentTransformer.importRequest(requestType));
			modifyCriteria(espd);
			return espd;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Optional.absent();
		}
	}

	/**
	 * Convert a {@link QualificationApplicationResponseType} coming from an input stream into a {@link ESPDDocument} object needed by
	 * the web application user interface.
	 *
	 * @param espdResponseStream An input stream containing the ESPD Response
	 *
	 * @return An {@link ESPDDocument} object coming out from the stream if it contained a valid ESPD Response
	 * wrapped in an {@link Optional} or an empty {@link Optional} if the import was unsuccessful.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ESPDDocument> importEspdResponse(InputStream espdResponseStream) {
		try {
			JAXBElement<QualificationApplicationResponseType> element = (JAXBElement<QualificationApplicationResponseType>) jaxb2Marshaller
					.unmarshal(new StreamSource(espdResponseStream));
			QualificationApplicationResponseType responseType = element.getValue();
			Optional<ESPDDocument> espd=Optional.of(responseToESPDDocumentTransformer.importResponse(responseType));
			modifyCriteria(espd);
			return espd;			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Optional.absent();
		}
	}

	/**
	 * Read from an input stream that should contain a {@link QualificationApplicationRequestType} or {@link QualificationApplicationResponseType}
	 * and convert it into a {@link ESPDDocument}. It is not known beforehand if the content
	 * belongs to a ESPD Request or Response.
	 *
	 * @param espdStream An input stream hopefully containing a ESPD Request or Response
	 *
	 * @return An {@link ESPDDocument} object coming out from the stream if it contained a valid ESPD Request or Response
	 * wrapped in an {@link Optional} or an empty {@link Optional} if the import was unsuccessful.
	 * @throws Exception 
	 *
	 * @throws TedNoticeException if file contains Ted Notice XML
	 */
	public Optional<ESPDDocument> importAmbiguousEspdFile(InputStream espdStream) throws Exception {
		// peek at the first bytes in the file to see if it is a ESPD Request or Response
		try (BufferedInputStream bis = new BufferedInputStream(espdStream)) {
			int peekReadLimit = 80;
			bis.mark(peekReadLimit);
			byte[] peek = new byte[peekReadLimit];
			int bytesRead = bis.read(peek, 0, peekReadLimit - 1);
			if (bytesRead < 0) {
				return Optional.absent();
			}
			bis.reset(); // need to read from the beginning afterwards
			String firstBytes = new String(peek, "UTF-8");

			// decide how to read the uploaded file
			if (firstBytes.contains("ESPDResponse")) {
				return importEspdResponse(bis);
			} else if (firstBytes.contains("ESPDRequest")) {
				return importEspdRequest(bis);
			} else if (firstBytes.contains("ContractNotice") || firstBytes.contains("TED_EXPORT")) {
				throw new Exception();
			}
		}
		return Optional.absent();
	}

	/**
	 * Merge the data coming from a ESPD Request with data coming from a ESPD Response.
	 * <p>
	 * The new file shall include:
	 * <ul>
	 * <li>Information on the EO</li>
	 * <li>All mandatory Exclusion grounds</li>
	 * <li>Selection criteria that where asked for in the new ESPD request that were answered in the old ESPD response</li>
	 * </ul>
	 * </p>
	 *
	 * @param requestStream  An input stream hopefully containing a ESPD Request
	 * @param responseStream An input stream hopefully containing a ESPD Response
	 *
	 * @return An {@link ESPDDocument} object coming out from the merging of the Request and Response
	 * wrapped in an {@link Optional} or an empty {@link Optional} if the import was unsuccessful.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ESPDDocument> mergeEspdRequestAndResponse(InputStream requestStream, InputStream responseStream) {
		try {
			JAXBElement<QualificationApplicationRequestType> requestElement = (JAXBElement<QualificationApplicationRequestType>) jaxb2Marshaller
					.unmarshal(new StreamSource(requestStream));
			QualificationApplicationRequestType requestType = requestElement.getValue();
			JAXBElement<QualificationApplicationResponseType> responseElement = (JAXBElement<QualificationApplicationResponseType>) jaxb2Marshaller
					.unmarshal(new StreamSource(responseStream));
			QualificationApplicationResponseType responseType = responseElement.getValue();
			return Optional.of(requestResponseMerger.mergeRequestAndResponse(requestType, responseType));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Optional.absent();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void modifyCriteria(Optional<ESPDDocument> espd) {
		
		List<DynamicRequirementGroup> empty = new ArrayList<DynamicRequirementGroup>();
		List<DynamicRequirementGroup> unboundedGroups = new ArrayList<DynamicRequirementGroup>();
		List<DynamicRequirementGroup> unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
		if(espd.get().getTechniciansTechnicalBodies()!= null && espd.get().getTechniciansTechnicalBodies().getUnboundedGroups().size() > 0) {
			for (DynamicRequirementGroup d : espd.get().getTechniciansTechnicalBodies().getUnboundedGroups()) {
				if(d.get("unboundedGroups") != null && ((List<DynamicRequirementGroup>)d.get("unboundedGroups")).size() > 0) {
					for (DynamicRequirementGroup d2 : ((List<DynamicRequirementGroup>)d.get("unboundedGroups"))) {
						if(d2.get("__nomeGruppo__") != null && d2.get("__nomeGruppo__").equals("unbound1")) {
							//log.info("da aggiugenre al gruppo 1");
							unboundedGroups.add(d2);
						}else {
							unboundedGroups2.add(d2);
							//log.info("da aggiugenre al gruppo 2");
						}
					}
					d.put("unboundedGroups",unboundedGroups);
					d.put("unboundedGroups2",unboundedGroups2);
					unboundedGroups = new ArrayList<DynamicRequirementGroup>();
					unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
				}				
			}
		}
		
		unboundedGroups = new ArrayList<DynamicRequirementGroup>();
		unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
		if(espd.get().getWorkContractsTechnicians()!= null && espd.get().getWorkContractsTechnicians().getUnboundedGroups().size() > 0) {
			for (DynamicRequirementGroup d : espd.get().getWorkContractsTechnicians().getUnboundedGroups()) {
				if(d.get("unboundedGroups") != null && ((List<DynamicRequirementGroup>)d.get("unboundedGroups")).size() > 0) {
					for (DynamicRequirementGroup d2 : ((List<DynamicRequirementGroup>)d.get("unboundedGroups"))) {
						if(d2.get("__nomeGruppo__") != null && d2.get("__nomeGruppo__").equals("unbound1")) {
							//log.info("da aggiugenre al gruppo 1");
							unboundedGroups.add(d2);
						}else {
							unboundedGroups2.add(d2);
							//log.info("da aggiugenre al gruppo 2");
						}
					}
					d.put("unboundedGroups",unboundedGroups);
					d.put("unboundedGroups2",unboundedGroups2);
					unboundedGroups = new ArrayList<DynamicRequirementGroup>();
					unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
				}				
			}
		}
		
		
		unboundedGroups = new ArrayList<DynamicRequirementGroup>();
		unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
		if(espd.get().getOtherEconomicFinancialRequirements()!= null && espd.get().getOtherEconomicFinancialRequirements().getUnboundedGroups().size() > 0) {
			
			List<DynamicRequirementGroup> listaUnboundedGroups = new ArrayList<DynamicRequirementGroup>();
			for (DynamicRequirementGroup d : espd.get().getOtherEconomicFinancialRequirements().getUnboundedGroups()) {
				if(!d.isEmpty()) {
					listaUnboundedGroups.add(d);
				}
			}
			espd.get().getOtherEconomicFinancialRequirements().setUnboundedGroups(listaUnboundedGroups);
			for (DynamicRequirementGroup d : espd.get().getOtherEconomicFinancialRequirements().getUnboundedGroups()) {
				if(d.get("unboundedGroups") != null && ((List<DynamicRequirementGroup>)d.get("unboundedGroups")).size() > 0) {
					for (DynamicRequirementGroup d2 : ((List<DynamicRequirementGroup>)d.get("unboundedGroups"))) {
						if(d2.get("__nomeGruppo__") != null && d2.get("__nomeGruppo__").equals("unbound1")) {
							//log.info("da aggiugenre al gruppo 1");
							unboundedGroups.add(d2);
						}else {
							unboundedGroups2.add(d2);
							//log.info("da aggiugenre al gruppo 2");
						}
					}
					d.put("unboundedGroups",unboundedGroups);
					d.put("unboundedGroups2",unboundedGroups2);
					unboundedGroups = new ArrayList<DynamicRequirementGroup>();
					unboundedGroups2 = new ArrayList<DynamicRequirementGroup>();
				}				
			}
		}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
