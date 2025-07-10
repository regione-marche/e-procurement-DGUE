package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting.UblRequestTypeTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblResponseTypeTransformer;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

/**
 * Class used to generate XML files containing ESPD Requests or Responses.
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Component
public class EspdXmlExporter {

	private final Jaxb2Marshaller jaxb2Marshaller;
	private final UblRequestTypeTransformer toEspdRequestTransformer;
	private final UblResponseTypeTransformer toEspdResponseTransformer;
	private final oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.ObjectFactory espdRequestObjectFactory;
	private final oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.ObjectFactory espdResponseObjectFactory;

	@Autowired
	EspdXmlExporter(Jaxb2Marshaller jaxb2Marshaller, UblRequestTypeTransformer toEspdRequestTransformer,
			UblResponseTypeTransformer toEspdResponseTransformer) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.toEspdRequestTransformer = toEspdRequestTransformer;
		this.toEspdResponseTransformer = toEspdResponseTransformer;
		this.espdRequestObjectFactory = new oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.ObjectFactory();
		this.espdResponseObjectFactory = new oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.ObjectFactory();
	}

	/**
	 * Create a {@link QualificationApplicationRequestType} from the provided {@link ESPDDocument} and marshals it
	 * to the output stream.
	 *
	 * @param espdDocument The ESPD document that will be written out
	 *
	 * @return The stream where the XML representation will be written out
	 */
	public ByteArrayOutputStream generateEspdRequest(ESPDDocument espdDocument) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		QualificationApplicationRequestType espdRequestType = toEspdRequestTransformer.buildRequestType(espdDocument);
		if(espdDocument.getOwner() == null || espdDocument.getOwner().equals(APIConstants.OWNER_CODE_EDITED)){
			espdRequestType.getID().setSchemeName(APIConstants.OWNER_CODE_EDITED);
		} else if(espdDocument.getOwner().equals(APIConstants.OWNER_CODE)){
			espdRequestType.getID().setSchemeName(APIConstants.OWNER_CODE);
		}

		StreamResult result = new StreamResult(out);
		jaxb2Marshaller.marshal(espdRequestObjectFactory.createQualificationApplicationRequest(espdRequestType), result);
		return out;
	}

	/**
	 * Create a {@link QualificationApplicationRequestType} from the provided {@link ESPDDocument} and marshals it
	 * as a {@link StringWriter}.
	 *
	 * @param espdDocument The ESPD document that will be written out
	 * @param sw           The place where the XML representation will be written out
	 */
	public void generateEspdRequest(ESPDDocument espdDocument, StringWriter sw) {
		QualificationApplicationRequestType espdRequestType = toEspdRequestTransformer.buildRequestType(espdDocument);
		StreamResult result = new StreamResult(sw);

		jaxb2Marshaller.marshal(espdRequestObjectFactory.createQualificationApplicationRequest(espdRequestType), result);
	}

	/**
	 * Create a {@link QualificationApplicationResponseType} from the provided {@link ESPDDocument} and marshals it
	 * to the output stream.
	 *
	 * @param espdDocument The ESPD document that will be written out
	 *
	 * @return The stream where the XML representation will be written out
	 */
	public ByteArrayOutputStream generateEspdResponse(ESPDDocument espdDocument) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		QualificationApplicationResponseType espdResponseType = toEspdResponseTransformer.buildResponseType(espdDocument);
		if(espdDocument.getOwner() == null || espdDocument.getOwner().equals(APIConstants.OWNER_CODE_EDITED)){
			espdResponseType.getID().setSchemeName(APIConstants.OWNER_CODE_EDITED);
		} else if(espdDocument.getOwner().equals(APIConstants.OWNER_CODE)){
			espdResponseType.getID().setSchemeName(APIConstants.OWNER_CODE);
		}
		StreamResult result = new StreamResult(out);
		jaxb2Marshaller.marshal(espdResponseObjectFactory.createQualificationApplicationResponse(espdResponseType), result);
		return out;
	}

	/**
	 * Create a {@link QualificationApplicationResponseType} from the provided {@link ESPDDocument} and marshals it
	 * to the output stream.
	 *
	 * @param espdDocument The ESPD document that will be written out
	 * @param sw           The place where the XML representation will be written out
	 */
	public void generateEspdResponse(ESPDDocument espdDocument, StringWriter sw) {
		QualificationApplicationResponseType espdResponseType = toEspdResponseTransformer.buildResponseType(espdDocument);
		StreamResult result = new StreamResult(sw);
		jaxb2Marshaller.marshal(espdResponseObjectFactory.createQualificationApplicationResponse(espdResponseType), result);
	}
	
	
		
}
