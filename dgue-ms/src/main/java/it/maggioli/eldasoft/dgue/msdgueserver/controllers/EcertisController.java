package it.maggioli.eldasoft.dgue.msdgueserver.controllers;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.maggioli.eldasoft.dgue.msdgueserver.core.APIConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EcertisInfoSearchForm;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ResponseEcertis;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@SuppressWarnings("java:S5122")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(APIConstants.API_PREFIX_V1)
@Tag(name = "Progetto DGUE e-certis controller", description = "Ecertis Controller")
public class EcertisController {


    @PostMapping("/getEcertisInfo")
    public ResponseEntity<ResponseEcertis> getEcertisInfo(@RequestBody EcertisInfoSearchForm info) {
    	ResponseEcertis re=new ResponseEcertis();
    	String subCriteriaHtml = "";
    	if("it".equals(info.getLang())) {
    		subCriteriaHtml = "<div><b>Informazioni relative a Italia su e-Certis</b></div><ol type=\"I\" id=\"list\" style=\"padding-left: 1em;\">";
    	} else {
    		subCriteriaHtml = "<div><b>Information relating to Italy on e-Certis</b></div><ol type=\"I\" id=\"list\" style=\"padding-left: 1em;\">";
    	}
    	
    	//re.setSubCriteria("<div><h1>HTML DI PROVA</h1><ol><li>primo elemento della lista</li><li>'+String+'</li><li>terzo</li></ol></div>");
    	int i = 1;
    	for (String string : info.getUuid()) {
    		try {
	    		
	            String html = "";
	     
//	            URL url = new URL("https://api.anticorruzione.it/cefecertis/1.0/api/v1/criteria/"+string+"?language=it");
//	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	            connection.setRequestProperty("accept", "application/json");
//	            InputStream responseStream = connection.getInputStream();
//	            ObjectMapper mapper = new ObjectMapper();
//	            Object apod = mapper.readValue(responseStream, Object.class);
//	            System.out.println(apod);
	            
	            
    			  Client client = getSSLClient();
    			  String url = "https://api.anticorruzione.it/cefecertis/1.0/api/v1/criteria/"+string+"?language="+info.getLang();
    			  WebTarget webTarget = client.target(url);
    			
    	  		  Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
    			  Response response = invocationBuilder.get( Response.class );
    			  int statusCode = response.getStatus();
    			  
    			  if(statusCode != Response.Status.OK.getStatusCode()){
    				  String responseObj = (response.readEntity( String.class )).toString();    				 
    			  } else {
    				  String responseObj = (response.readEntity( String.class )).toString();
    				  ObjectMapper mapper = new ObjectMapper();
    				  JsonNode rootNode = mapper.readTree(responseObj);
    				 
    				  String name = rootNode.get("name").asText();
    				  String description = rootNode.get("description").asText();
    				  html="<li class=\"sub-ecertis\" ><div class=\"subcriteria-name\">"+name+"</div><div class=\"subcriteria-description\">"+description+"</div></li>";
    				  subCriteriaHtml=subCriteriaHtml+html;
    			  }
    			  
    			 
    			 
	            
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				log.error("Si è verificato un errore nel metodo: getEcertisInfo",e);
			}
    		i++;
		}
    	subCriteriaHtml=subCriteriaHtml+"</ol>";
    	re.setSubCriteria(subCriteriaHtml);
    	
    	return ResponseEntity.ok().body(re);
    }
	

    private Client getSSLClient() throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			@SuppressWarnings("java:S4830")
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			@SuppressWarnings("java:S4830")
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

		} }, new SecureRandom());

		@SuppressWarnings("java:S5527")
		Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
		return client;
	}
   
}
