/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2009 Griffin Brown Digitial Publishing Ltd
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package it.maggioli.eldasoft.dgue.msdgueserver.schematron;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.xslt.SchematronResourceSCH;


/**
 * Class to support command-line invocation of Probatron4J.
 */
public class SchematronValidator
{

    private static Logger logger = LoggerFactory.getLogger( SchematronValidator.class );


    
    
    public List<String> validitorRequest(byte[] espdFile) {
    		    		
        	
    	List<String> schematronError = new ArrayList<String>(); 
    	//DA RIMUOVERE 2 RIGHE SOTTO PER VALIDARE XML
    	if(true)
    		return schematronError;
    	try {
    		
    		SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/01 ESPD Common CL Attributes.sch"), new ByteArrayInputStream(espdFile));            	
        	for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
    	}catch (Exception e) {
    		logger.error("{}",e.getMessage());
		}
    	try {
    		SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/01 ESPD Common CL Values Restrictions.sch"), new ByteArrayInputStream(espdFile));
    		for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
    	}catch (Exception e) {
    		logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/01 ESPD-codelist-values.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/02 ESPD Req Cardinality BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/03 ESPD Common Criterion BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/03 ESPD Req Criterion BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/04 ESPD Common Other BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/04 ESPD Req Other BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}	
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/05 ESPD Req Extended BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}	
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/request/05 ESPD Req Procurer BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {				
			logger.error("{}",e.getMessage());
		}	
        	        	  
			 
			  
        return schematronError;      
    }

   
    public List<String> validitorResponse(byte[] espdFile) {
        
    	
    	List<String> schematronError = new ArrayList<String>();
    	//DA RIMUOVERE 2 RIGHE SOTTO PER VALIDARE XML
    	if(true)
    		return schematronError;
    	File espdResponse = new File("src/main/resources/schematron/response/response.xml");
    	try {
    		SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/01 ESPD Common CL Attributes.sch"), new ByteArrayInputStream(espdFile));            	
        	for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
    	}catch (Exception e) {
    		logger.error("{}",e.getMessage());
		}
    	try {
    		SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/01 ESPD Common CL Values Restrictions.sch"), new ByteArrayInputStream(espdFile));
    		for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
    	}catch (Exception e) {
    		logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/01 ESPD-codelist-values.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/02 ESPD Req Cardinality BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/03 ESPD Common Criterion BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/03 ESPD Req Criterion BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/04 ESPD Common Other BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/04 ESPD Req Other BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}	
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/05 ESPD Req Extended BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {
			logger.error("{}",e.getMessage());
		}	
		try {
			SchematronOutputType messages = validateXMLViaXSLTSchematronFull(new File("src/main/resources/schematron/response/05 ESPD Req Procurer BR.sch"), new ByteArrayInputStream(espdFile));
			for (Object iterable_element : messages.getActivePatternAndFiredRuleAndFailedAssert()) {					
				if(iterable_element.getClass() == FailedAssert.class ) {
					if(iterable_element != null && ((FailedAssert)iterable_element).getText() != null) {
					schematronError.add(((FailedAssert)iterable_element).getText().toString());
					}
				}
			}
		}catch (Exception e) {				
			logger.error("{}",e.getMessage());
		}    	        	  	
		  
    return schematronError;      
}
    
    public static SchematronOutputType validateXMLViaXSLTSchematronFull (final File aSchematronFile,final InputStream aXMLFile) throws Exception
    {
      final ISchematronResource aResSCH = SchematronResourceSCH.fromFile(aSchematronFile);
      if (!aResSCH.isValidSchematron())
        throw new IllegalArgumentException("Invalid Schematron!");      
      return aResSCH.applySchematronValidationToSVRL(new StreamSource(aXMLFile));
    }
    
//    public static boolean validateXMLViaXSLTSchematron ( final File aSchematronFile,  final File aXMLFile) throws Exception
//    {
//      final ISchematronResource aResSCH = SchematronResourceSchXslt_XSLT2.fromFile(aSchematronFile);
//      if (!aResSCH.isValidSchematron())
//        throw new IllegalArgumentException("Invalid Schematron!");
//      return aResSCH.getSchematronValidity(new StreamSource(aXMLFile)).isValid ();
//    }
    

  
    
    

}
