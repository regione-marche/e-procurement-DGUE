package it.maggioli.eldasoft.dgue.msdgueserver.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.CustomErrorResponse;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDBase64;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDInfoResponse;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.EspdXmlInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("java:S5122")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(APIConstants.API_PREFIX_V1)
@Tag(name = "Progetto DGUE Info Controller", description = "ESPD Info Controller")
public class ESPDInfoController {

	 @Autowired
	 private EspdXmlInfo xmlInfo = null;
	 
	@PostMapping(value = "/info")
    @ResponseBody
    public ResponseEntity<ESPDInfoResponse> getXmlInfo(@RequestBody @Valid ESPDBase64 document) {
		InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(document.getBase64Xml().getBytes()));
		ESPDInfoResponse infoResponse = new ESPDInfoResponse();
		long nowMillisBefore = System.currentTimeMillis();
		try {
			infoResponse.setInfo(xmlInfo.xmlInfo(stream,document.getNameFile()));
		} catch (UnmarshallingFailureException ex) {
			log.error("si è verificato un errore nel metodo getXmlInfo UnmarshallingFailureException",ex);
			CustomErrorResponse errors = new CustomErrorResponse();
		    errors.setTimestamp(LocalDateTime.now());		   
		    errors.setStatus(HttpStatus.BAD_REQUEST.value());		   
		    errors.setError(ESPDInfoResponse.ERRORE_UNMARSHALLING+" - "+ex.getMessage());		    
		    infoResponse.setError(errors);
		    return new ResponseEntity<ESPDInfoResponse>(infoResponse,HttpStatus.BAD_REQUEST);
		} catch (Exception e) {	
			log.error("si è verificato un errore nel metodo getXmlInfo Exception",e);
			CustomErrorResponse errors = new CustomErrorResponse();
		    errors.setTimestamp(LocalDateTime.now());		   
		    errors.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());		    
		    errors.setError(ESPDInfoResponse.ERRORE_LETTURA+" - "+e.getMessage());		    
		    infoResponse.setError(errors);
		    return new ResponseEntity<ESPDInfoResponse>(infoResponse,HttpStatus.INTERNAL_SERVER_ERROR);
			//  return new ResponseEntity<ESPDInfo>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

		long nowMillisAfter = System.currentTimeMillis();
		
		
		int seconds = (int) (nowMillisAfter - nowMillisBefore);
		System.out.println(seconds);
		log.info("[xmlInfo] tempo impiegato in millisecondi: "+seconds);
		return ResponseEntity.ok().body(infoResponse);
					 	
    }
	
	
	@GetMapping(value = "/status")
    @ResponseBody
    public ResponseEntity<HttpStatus> getStatus() {	
		HttpStatus status = HttpStatus.OK;
		return ResponseEntity.ok(status);
					 	
    }
}
