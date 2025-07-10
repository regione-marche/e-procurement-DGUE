package it.maggioli.eldasoft.dgue.msdgueserver.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ImportedXml;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ResponseEspd;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.EspdXmlExporter;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.EspdXmlImporter;
//import it.maggioli.eldasoft.dgue.msdgueserver.schematron.SchematronValidator;
//import it.maggioli.eldasoft.dgue.msdgueserver.vo.XMLValidationVO;
import it.maggioli.eldasoft.dgue.msdgueserver.schematron.SchematronValidator;
import it.maggioli.eldasoft.dgue.msdgueserver.vo.XMLValidationVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("java:S5122")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(APIConstants.API_PREFIX_V1)
@Tag(name = "Progetto DGUE Response Controller", description = "ESPD Response Controller")
public class ESPDResponseController {

    @Autowired
    private EspdXmlExporter espdXmlExporter = null;
    
    @Autowired
    private EspdXmlImporter xmlImporter = null;
    
    @Value("${validateXml}")
	private boolean validateXml;
    
    ESPDDocument doc;

    @PostMapping(value = "/response/export")
    @ResponseBody
    public ResponseEntity<Resource> exportResponse(@Valid @RequestBody ESPDDocument document) {
    	doc=document; 
    	SchematronValidator schematronValidator=new SchematronValidator(); 
        final ByteArrayOutputStream out = espdXmlExporter.generateEspdResponse(document);
        final Resource response = new ByteArrayResource(out.toByteArray());             
        List<String> schematronError = new ArrayList<String>();
        Boolean valid = true;
        schematronError = schematronValidator.validitorResponse(out.toByteArray());			
		valid = schematronError.size()  == 0 ? true : false;
		log.info("espd valid: ",valid);
        // VALUTARE GESTIONE ERRORI
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"espd-response.xml\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .contentLength(out.size())
                .body(response);
    }
    
    
    
    @PostMapping(value = "/response/import")
    @ResponseBody
    public ResponseEntity<ResponseEspd> importRequest(@RequestBody ImportedXml document) {
    	ResponseEspd v=new ResponseEspd();
    	
    	SchematronValidator schematronValidator=new SchematronValidator(); 
    
        Optional<ESPDDocument> espd= null;
        if(document.getXml() != null){            
	        if(validateXml) {        
	        	if(document.getWhoIs().equals("OE")) {
	        		List<String> schematronError = new ArrayList<String>();
	        		Boolean valid = true;
	        		byte[] espdFile = Base64.decodeBase64(document.getXml());
					schematronError = schematronValidator.validitorResponse(espdFile);
					        			
					valid = schematronError.size()  == 0 ? true : false;
					log.info("espd valid: ",valid);
	        		System.out.println("converto per oe");
	        	}
	        }
			
			try {
				String string = new String(document.getXml());			
				ByteArrayInputStream  input = getInputStream(string, "UTF-8");					
						
				espd = xmlImporter.importEspdResponse(input);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("errore nel metodo: importRequest",e);
			}
			v.setEspd(espd.get());
        } else {
        	InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(document.getBase64Xml().getBytes()));
        	
        	List<String> schematronError = new ArrayList<String>();
    		Boolean valid = true;
            
			schematronError = schematronValidator.validitorResponse(Base64.decodeBase64(document.getBase64Xml().getBytes()));
			        			
			valid = schematronError.size()  == 0 ? true : false;
			log.info("espd valid: ",valid);
    		System.out.println("converto per oe");
			
			espd = xmlImporter.importEspdResponse(stream);						
			v.setEspd(espd.get());
			return ResponseEntity.ok().body(v);
        }
		
        return ResponseEntity.ok().body(v);
		
    }
    
    public String convertStreamToString(InputStream is) { 
    	  BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
    	  StringBuilder sb = new StringBuilder(); 

    	  String line = null; 

    	  try { 
    	    while ((line = reader.readLine()) != null) { 
    	    sb.append(line + "\n"); 
    	    } 
    	  } catch (IOException e) { 
    		  log.error("errore nel metodo: convertStreamToString",e);
    	  } finally { 
    	    try { 
    	      is.close(); 
    	    } catch (IOException e) { 
    	    	log.error("errore nel metodo: convertStreamToString",e);
    	    } 
    	  }

    	  return sb.toString(); 
    	} 
    
    
    public static ByteArrayInputStream getInputStream(String str, String encoding) throws          UnsupportedEncodingException {
        return new ByteArrayInputStream(str.getBytes(encoding));
     }
}
