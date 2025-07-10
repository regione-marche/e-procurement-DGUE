package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting;

import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isNotBlank;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.stereotype.Component;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Agency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CriterionElementType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ResponseDataType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcExpectedResponse;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcResponseType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.CommonUblFactory;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblRequirementTypeTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblRequirementFactory;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response.UblResponseRequirementTransformer;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.PropertyUtils;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceSuppliedType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseValueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CertificationLevelDescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedDescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedValueNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.MaximumValueNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.MinimumValueNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseURIType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValidatedCriterionPropertyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueDataTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueUnitCodeType;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
@Slf4j
@Component
class UblRequestRequirementTransformer extends UblRequirementTypeTemplate {

	
	@Override
	protected TenderingCriterionPropertyType buildRequirementType(String fiscalCode,
																  CacCriterionRequirement cacRequirement,
																  ESPDCriterion espdCriterion,
																  CacRequirementGroup group,
																  DynamicRequirementGroup dynamicRequirementGroup,
																  int unboundedGroupIndex,
																  String evidenceCode,
																  List<TenderingCriterionResponseType> tcrList) {
		final TenderingCriterionPropertyType requirementType = new TenderingCriterionPropertyType();
		
		//final IDType idType = CommonUblFactory.buildIdTypeValue(cacRequirement.getId());
		
		final IDType idType = CommonUblFactory.buildIdTypeValue(UUID.randomUUID().toString());
		idType.setSchemeID("ISO/IEC 9834-8:2008");		
		idType.setSchemeAgencyName(fiscalCode);
		idType.setSchemeVersionID("4");
		idType.setSchemeAgencyID("#");
		if(cacRequirement.getId().startsWith("IT")) {								
			idType.setValue("IT-"+idType.getValue());
		}			
		
		requirementType.setID(idType);

		final NameType nameType = new NameType();
		nameType.setValue(cacRequirement.getName());
		requirementType.setName(nameType);

		final DescriptionType descriptionType = new DescriptionType();
		descriptionType.setValue(cacRequirement.getDescription());
		requirementType.getDescription().add(descriptionType);

		buildResponseData(requirementType, cacRequirement, fiscalCode,espdCriterion,dynamicRequirementGroup);
		if(tcrList != null) {	
			if(!"REQUIREMENT".equals(cacRequirement.getExpectedResponse().getCriterionElement().getCode()) &&
					!"CAPTION".equals(cacRequirement.getExpectedResponse().getCriterionElement().getCode())) {	
				TenderingCriterionResponseType resp=buildRequirementTypeResponse(fiscalCode,cacRequirement, espdCriterion,dynamicRequirementGroup, group, unboundedGroupIndex, evidenceCode,idType.getValue());							
				tcrList.add(resp);
				
			}
		}
		
		return requirementType;
	}

	

	private void buildResponseData(final TenderingCriterionPropertyType requirementType, final CacCriterionRequirement cacRequirement, final String fiscalCode, final ESPDCriterion espdCriterion, final DynamicRequirementGroup dynamicRequirementGroup) {
		final TypeCodeType typeCodeType = new TypeCodeType();
		final CbcExpectedResponse expectedResponse = cacRequirement.getExpectedResponse();
		final CriterionElementType criterionElementType = expectedResponse.getCriterionElement();
		String variableName2 = null;
		final String variableName=cacRequirement.getEspdCriterionFields().get(0);
		if(cacRequirement.getEspdCriterionFields().size() > 1) {
		
			variableName2=cacRequirement.getEspdCriterionFields().get(1);
		}
		
		if(criterionElementType != null) {			
			typeCodeType.setValue(criterionElementType.getCode());
			typeCodeType.setListID(criterionElementType.getListId());
			//typeCodeType.setListAgencyName(criterionElementType.getListAgencyName());
			typeCodeType.setListAgencyName(Agency.EU_COM_GROW.getLongName());
			typeCodeType.setListAgencyID(Agency.EU_COM_GROW.getIdentifier());
			typeCodeType.setListVersionID(criterionElementType.getListVersionId());
			requirementType.setTypeCode(typeCodeType);
			final ValueDataTypeCodeType dataTypeCodeType = new ValueDataTypeCodeType();
			
			ResponseDataType responseDataType = ResponseDataType.NONE;
			if (expectedResponse != null) {
				responseDataType = expectedResponse.getResponseDataType();
			}
			
			if (CriterionElementType.QUESTION.getCode().equals(criterionElementType.getCode())) {
				if (ResponseDataType.NONE.getCode().equals(expectedResponse.getResponseDataType().getCode())) {
					throw new IllegalArgumentException("Question must have not NONE as ResponseDataType");
				}
				responseDataType = expectedResponse.getResponseDataType();
				
				if (isNotBlank(expectedResponse.getUnitCode())) {
					final ValueUnitCodeType valueUnitCodeType = new ValueUnitCodeType();
					valueUnitCodeType.setValue(expectedResponse.getUnitCode());
					requirementType.setValueUnitCode(valueUnitCodeType);
				}
				
				if (expectedResponse.getCurrency() != null  && isNotBlank(expectedResponse.getCurrency().getCode())) {
					final ValueCurrencyCodeType valueCurrencyCodeType = new ValueCurrencyCodeType();
					valueCurrencyCodeType.setValue(expectedResponse.getCurrency().getCode());
					valueCurrencyCodeType.setListID(expectedResponse.getCurrency().getListId());
					valueCurrencyCodeType.setListAgencyName(expectedResponse.getCurrency().getListAgencyName());
					valueCurrencyCodeType.setListVersionID(expectedResponse.getCurrency().getListVersionId());
					requirementType.setValueCurrencyCode(valueCurrencyCodeType);
				}
				
//				final ExpectedIDType expectedIDType = new ExpectedIDType();
//				expectedIDType.setValue(UUID.randomUUID().toString());
//				//expectedIDType.setSchemeID("ISO/IEC 9834-8:2008");
//				expectedIDType.setSchemeID("EU-COM-GROW");
//				expectedIDType.setSchemeAgencyName(fiscalCode);
//				//expectedIDType.setSchemeVersionID("4");
//				expectedIDType.setSchemeVersionID("2.1.1");
//				requirementType.setExpectedID(expectedIDType);
				
				
				if (expectedResponse.getExpectedCode() != null && isNotBlank(expectedResponse.getExpectedCode().getCode())) {
					final ExpectedCodeType expectedCodeType = new ExpectedCodeType();
					expectedCodeType.setValue(expectedResponse.getExpectedCode().getCode());
					expectedCodeType.setListID(expectedResponse.getExpectedCode().getListId());
					expectedCodeType.setListAgencyName(expectedResponse.getExpectedCode().getListAgencyName());
					expectedCodeType.setListVersionID(expectedResponse.getExpectedCode().getListVersionId());
					requirementType.setExpectedCode(expectedCodeType);
				}
				
				if (expectedResponse.getExpectedValueNumeric() != null) {
					final ExpectedValueNumericType expectedValueNumericType = new ExpectedValueNumericType();
					expectedValueNumericType.setValue(expectedResponse.getExpectedValueNumeric());
					requirementType.setExpectedValueNumeric(expectedValueNumericType);
				}
				
				if (expectedResponse.getMaximumValueNumeric() != null) {
					final MaximumValueNumericType maximumValueNumericType = new MaximumValueNumericType();
					maximumValueNumericType.setValue(expectedResponse.getMaximumValueNumeric());
					requirementType.setMaximumValueNumeric(maximumValueNumericType);
				}
				
				if (expectedResponse.getMinimumValueNumeric() != null) {
					final MinimumValueNumericType minimumValueNumericType = new MinimumValueNumericType();
					minimumValueNumericType.setValue(expectedResponse.getMinimumValueNumeric());
					requirementType.setMinimumValueNumeric(minimumValueNumericType);
				}
				
				if (isNotBlank(expectedResponse.getCertificationLevelDesc())) {
					final CertificationLevelDescriptionType certificationLevelDescriptionType = new CertificationLevelDescriptionType();
					certificationLevelDescriptionType.setValue(expectedResponse.getCertificationLevelDesc());
					requirementType.getCertificationLevelDescription().add(certificationLevelDescriptionType);
				}
			}
			
			

			try {
				
				if(criterionElementType.getCode().equals("REQUIREMENT")) {
					if(dynamicRequirementGroup == null) {							
						if(espdCriterion != null) {								
							Object value = PropertyUtils.getProperty(espdCriterion, variableName);
							if(value != null) {							
								if(responseDataType.getCode().equals("LOT_IDENTIFIER")) {
									ExpectedIDType eid=new ExpectedIDType();
									eid.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									eid.setSchemeAgencyID("EU-COM-GROW");
									requirementType.setExpectedID(eid);
								}
								else if(responseDataType.getCode().equals("ECONOMIC_OPERATOR_IDENTIFIER")) {
									ExpectedIDType eid=new ExpectedIDType();
									eid.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									eid.setSchemeAgencyID("EU-COM-GROW");
									requirementType.setExpectedID(eid);
								}
								else if(responseDataType.getCode().equals("DESCRIPTION")) {
									ExpectedDescriptionType edt=new ExpectedDescriptionType();
									edt.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									requirementType.setExpectedDescription(edt);
								}				
								else if(responseDataType.getCode().equals("AMOUNT")) {
									ExpectedAmountType eat=new ExpectedAmountType();
									BigDecimal bd;
									if(value instanceof String) {
										bd=new BigDecimal((String) value);		
										eat.setValue(bd);
									}else if(value instanceof Integer) {
										eat.setValue(BigDecimal.valueOf((Integer)value));
									}else if(value instanceof Double) {
										eat.setValue(BigDecimal.valueOf((Double)value));
									} else {
										eat.setValue((BigDecimal) value);								
									}	
									if(cacRequirement.getEspdCriterionFields().size()>1) {								
										String currency=cacRequirement.getEspdCriterionFields().get(1);
										eat.setCurrencyID((String) PropertyUtils.getProperty(espdCriterion, currency));
										eat.setCurrencyCodeListVersionID((String) PropertyUtils.getProperty(espdCriterion, currency));
									}
									requirementType.setExpectedAmount(eat);
								}
								else if(responseDataType.getCode().contains("QUANTITY")) {
									ExpectedValueNumericType eat=new ExpectedValueNumericType();
									BigDecimal bd;
									if(value instanceof String) {
										bd=new BigDecimal((String) value);		
										eat.setValue(bd);
									}else if(value instanceof Integer) {
										eat.setValue(BigDecimal.valueOf((Integer)value));
									} else if(value instanceof Double) {
											eat.setValue(BigDecimal.valueOf((Double)value));
									} else {
										eat.setValue((BigDecimal) value);								
									}
									
									requirementType.setExpectedValueNumeric(eat);
								}
								else if(responseDataType.getCode().contains("CODE_BOOLEAN")) {
									ExpectedCodeType ec=new ExpectedCodeType();
									ec.setValue(String.valueOf((Boolean) PropertyUtils.getProperty(espdCriterion, variableName)));
									ec.setListID("PleaseSelectTheCorrectOne"); 
									ec.setListAgencyID("EU-COM-GROW"); 
									ec.setListVersionID("2.1.1");
									requirementType.setExpectedCode(ec);			
								}
								else if(responseDataType.getCode().contains("CODE")) {
									ExpectedCodeType ec=new ExpectedCodeType();
									ec.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									ec.setListID("PleaseSelectTheCorrectOne"); 
									ec.setListAgencyID("EU-COM-GROW"); 
									ec.setListVersionID("2.1.1");
									requirementType.setExpectedCode(ec);
								}
								else if(responseDataType.getCode().equals("WEIGHT_INDICATOR")) {
									ExpectedCodeType ec=new ExpectedCodeType();
									if(PropertyUtils.getProperty(espdCriterion, variableName) instanceof Boolean) {											
										ec.setValue(String.valueOf((Boolean)PropertyUtils.getProperty(espdCriterion, variableName)));
									}else {
										ec.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									}
									ec.setListID("PleaseSelectTheCorrectOne"); 
									ec.setListAgencyID("EU-COM-GROW"); 
									ec.setListVersionID("2.1.1");
									requirementType.setExpectedCode(ec);
								}
								else if(responseDataType.getCode().contains("URL")) {
									ExpectedDescriptionType edt=new ExpectedDescriptionType();
									edt.setValue((String) PropertyUtils.getProperty(espdCriterion, variableName));
									requirementType.setExpectedDescription(edt);							
								}
								else if(responseDataType.getCode().contains("PERIOD")) {
									
									PeriodType pt=new PeriodType();
									StartDateType sdt=new StartDateType();
									EndDateType edt=new EndDateType();
									
									GregorianCalendar startDate = new GregorianCalendar();
									Date date=new SimpleDateFormat("yyyy-MM-dd").parse((String) PropertyUtils.getProperty(espdCriterion, variableName)); 
									startDate.setTime(date);
									
									
									GregorianCalendar endDate = new GregorianCalendar();
									Date date2=new SimpleDateFormat("yyyy-MM-dd").parse((String) PropertyUtils.getProperty(espdCriterion, variableName2)); 
									endDate.setTime(date2);
									
									
									sdt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate));
									edt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate));
									pt.setStartDate(sdt);
									pt.setEndDate(edt);
									
									
									requirementType.getApplicablePeriod().add(pt);
								}
								
							}
						}
					}else {
						Object value = dynamicRequirementGroup.get(variableName);
						if(value != null) {
								
							if(responseDataType.getCode().equals("LOT_IDENTIFIER")) {
								ExpectedIDType eid=new ExpectedIDType();
								eid.setValue((String) dynamicRequirementGroup.get(variableName));
								eid.setSchemeAgencyID("EU-COM-GROW");								
								requirementType.setExpectedID(eid);
							}
							else if(responseDataType.getCode().equals("ECONOMIC_OPERATOR_IDENTIFIER")) {
								ExpectedIDType eid=new ExpectedIDType();
								eid.setValue((String) dynamicRequirementGroup.get(variableName));
								eid.setSchemeAgencyID("EU-COM-GROW");
								requirementType.setExpectedID(eid);
							}
							else if(responseDataType.getCode().equals("DESCRIPTION")) {
								ExpectedDescriptionType edt=new ExpectedDescriptionType();
								edt.setValue((String) dynamicRequirementGroup.get(variableName));
								requirementType.setExpectedDescription(edt);
							}				
							else if(responseDataType.getCode().equals("AMOUNT")) {
								ExpectedAmountType eat=new ExpectedAmountType();
								BigDecimal bd;
								if(dynamicRequirementGroup.get(variableName) instanceof String) {
									bd=new BigDecimal((String) dynamicRequirementGroup.get(variableName));		
									eat.setValue(bd);
								}else if(dynamicRequirementGroup.get(variableName) instanceof Integer) {
									eat.setValue(BigDecimal.valueOf((Integer)dynamicRequirementGroup.get(variableName)));								
								}else if(dynamicRequirementGroup.get(variableName) instanceof Double) {
									eat.setValue(BigDecimal.valueOf((Double)dynamicRequirementGroup.get(variableName)));								
								} else {
									eat.setValue((BigDecimal) dynamicRequirementGroup.get(variableName));																	
								}
								
								if(cacRequirement.getEspdCriterionFields().size()>1) {								
									String currency=cacRequirement.getEspdCriterionFields().get(1);
									eat.setCurrencyID((String) dynamicRequirementGroup.get(currency));
									eat.setCurrencyCodeListVersionID((String) dynamicRequirementGroup.get(currency));
								}
								requirementType.setExpectedAmount(eat);
							}
							else if(responseDataType.getCode().contains("QUANTITY")) {
								ExpectedValueNumericType eat=new ExpectedValueNumericType();
								BigDecimal bd;
								if(dynamicRequirementGroup.get(variableName) instanceof String) {
									bd=new BigDecimal((String) dynamicRequirementGroup.get(variableName));		
									eat.setValue(bd);
								}else if(dynamicRequirementGroup.get(variableName) instanceof Integer) {
									eat.setValue(BigDecimal.valueOf((Integer)dynamicRequirementGroup.get(variableName)));								
								}else if(dynamicRequirementGroup.get(variableName) instanceof Double) {
										eat.setValue(BigDecimal.valueOf((Double)dynamicRequirementGroup.get(variableName)));								
								} else {
									eat.setValue((BigDecimal) dynamicRequirementGroup.get(variableName));																	
								}
								requirementType.setExpectedValueNumeric(eat);
							}
							else if(responseDataType.getCode().contains("CODE_BOOLEAN")) {
								ExpectedCodeType ec=new ExpectedCodeType();
								if(dynamicRequirementGroup.get(variableName) instanceof String) {									
									ec.setValue(String.valueOf((String) dynamicRequirementGroup.get(variableName)));
								} else if(dynamicRequirementGroup.get(variableName) instanceof Boolean) {									
									ec.setValue(String.valueOf((Boolean) dynamicRequirementGroup.get(variableName)));
								}
								ec.setListID("PleaseSelectTheCorrectOne"); 
								ec.setListAgencyID("EU-COM-GROW"); 
								ec.setListVersionID("2.1.1");
								requirementType.setExpectedCode(ec);			
							}
							else if(responseDataType.getCode().contains("CODE")) {
								ExpectedCodeType ec=new ExpectedCodeType();
								ec.setValue((String) dynamicRequirementGroup.get(variableName));
								ec.setListID("PleaseSelectTheCorrectOne"); 
								ec.setListAgencyID("EU-COM-GROW"); 
								ec.setListVersionID("2.1.1");
								requirementType.setExpectedCode(ec);
							}
							else if(responseDataType.getCode().equals("WEIGHT_INDICATOR")) {
								ExpectedCodeType ec=new ExpectedCodeType();
								if(dynamicRequirementGroup.get(variableName) instanceof String) {									
									ec.setValue(String.valueOf((String) dynamicRequirementGroup.get(variableName)));
								} else if(dynamicRequirementGroup.get(variableName) instanceof Boolean) {									
									ec.setValue(String.valueOf((Boolean) dynamicRequirementGroup.get(variableName)));
								}
								ec.setListID("PleaseSelectTheCorrectOne"); 
								ec.setListAgencyID("EU-COM-GROW"); 
								ec.setListVersionID("2.1.1");
								requirementType.setExpectedCode(ec);
							}
							else if(responseDataType.getCode().contains("URL")) {
								ExpectedDescriptionType edt=new ExpectedDescriptionType();
								edt.setValue((String) dynamicRequirementGroup.get(variableName));
								requirementType.setExpectedDescription(edt);
							}
							else if(responseDataType.getCode().contains("PERIOD")) {
								
								PeriodType pt=new PeriodType();
								StartDateType sdt=new StartDateType();
								EndDateType edt=new EndDateType();
								
								GregorianCalendar startDate = new GregorianCalendar();
								Date date=new SimpleDateFormat("yyyy-MM-dd").parse((String) dynamicRequirementGroup.get(variableName)); 
								startDate.setTime(date);
								
								
								GregorianCalendar endDate = new GregorianCalendar();
								Date date2=new SimpleDateFormat("yyyy-MM-dd").parse((String) dynamicRequirementGroup.get(variableName2)); 
								endDate.setTime(date2);
								
								
								sdt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate));
								edt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate));
								pt.setStartDate(sdt);
								pt.setEndDate(edt);
								
								
								requirementType.getApplicablePeriod().add(pt);
							}
							
						}
					}
				}
			}catch (Exception e) {
				log.error("errore nel metodo buildResponseData",e);				
			}
			
			
			dataTypeCodeType.setValue(responseDataType.getCode());
			//dataTypeCodeType.setListAgencyName(responseDataType.getListAgencyName());
			dataTypeCodeType.setListAgencyName(Agency.EU_COM_GROW.getLongName());
			dataTypeCodeType.setListID(responseDataType.getListId());
			dataTypeCodeType.setListAgencyID(Agency.EU_COM_GROW.getIdentifier());
			dataTypeCodeType.setListVersionID(responseDataType.getListVersionId());
			requirementType.setValueDataTypeCode(dataTypeCodeType);
		}

	}


	@Override
	protected TenderingCriterionResponseType buildRequirementTypeResponse(String fiscalCode,
			CacCriterionRequirement ccvRequirement, ESPDCriterion espdCriterion,DynamicRequirementGroup dynamicRequirementGroup, CacRequirementGroup requirementGroup,
			int unboundedGroupIndex, String evidenceCode,String questionId) {
		TenderingCriterionResponseType requirementType = new TenderingCriterionResponseType();
		
		IDType idType = CommonUblFactory.buildIdType(fiscalCode);
		idType.setValue(UUID.randomUUID().toString());
		idType.setSchemeID("ISO/IEC 9834-8:2008");
		idType.setSchemeVersionID("4");
		requirementType.setID(idType);
	
		requirementType.getDescription().add(UblRequirementFactory.buildDescriptionType(ccvRequirement.getDescription()));
		
		ValidatedCriterionPropertyIDType vcId=new ValidatedCriterionPropertyIDType();
		
		vcId.setValue(questionId);
		vcId.setSchemeID("ISO/IEC 9834-8:2008");
		vcId.setSchemeVersionID("4");
		vcId.setSchemeAgencyID("#");
		vcId.setSchemeAgencyName(fiscalCode);
		requirementType.setValidatedCriterionPropertyID(vcId);
		if(evidenceCode != null && !"".equals(evidenceCode)) {
			EvidenceSuppliedType evType=new EvidenceSuppliedType();
			IDType id=new IDType();
			id.setSchemeAgencyID("EU-COM-GROW");
			id.setValue(evidenceCode);
			evType.setID(id);
			requirementType.getEvidenceSupplied().add(evType);
		}else {	
			if("PERIOD".equals(ccvRequirement.getResponseType().getCode())) {
				Object st = readRequirementFirstValue(ccvRequirement, espdCriterion, dynamicRequirementGroup, requirementGroup, unboundedGroupIndex);
				Object ed = readRequirementSecondValue(ccvRequirement, espdCriterion,dynamicRequirementGroup, requirementGroup, unboundedGroupIndex);
				try {
					if (st != null || ed != null) {
						String start = "";
						String end = "";
						if(st instanceof Date ) {
							Instant instant = ((Date) st).toInstant();
							start = instant.toString();  
						}else {
							start = (String)st;
						}
						if(ed instanceof Date ) {
							Instant instant = ((Date) ed).toInstant();
							end = instant.toString();
						}else {
							end = (String)ed;
						}

						PeriodType pt=new PeriodType();
						StartDateType sdt=new StartDateType();
						EndDateType edt=new EndDateType();
						
						GregorianCalendar startDate = new GregorianCalendar();
						Date date;
						if(start != null) {
							
							date = new SimpleDateFormat("yyyy-MM-dd").parse((String) start);
							
							startDate.setTime(date);
						}
						
						
						GregorianCalendar endDate = new GregorianCalendar();

						if(end != null) {													
							Date date2=new SimpleDateFormat("yyyy-MM-dd").parse((String) end); 
							endDate.setTime(date2);
						}
						
						
						sdt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate));
						edt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate));
						pt.setStartDate(sdt);
						pt.setEndDate(edt);
						
						
						requirementType.getApplicablePeriod().add(pt);
					}
				} catch (ParseException | DatatypeConfigurationException e) {
					log.error("",e);
				} 
			}else {
			
				requirementType.getResponseValue().addAll(buildResponse(fiscalCode,ccvRequirement, espdCriterion,dynamicRequirementGroup, requirementGroup, unboundedGroupIndex));
			}
		}
		if(requirementType.getApplicablePeriod().isEmpty() && requirementType.getEvidenceSupplied().isEmpty() && requirementType.getResponseValue().isEmpty()) {
			requirementType = null;
		}
		return requirementType;
	}

	private List<ResponseValueType> buildResponse(String fiscalCode, CacCriterionRequirement ccvRequirement, ESPDCriterion ESPDCriterion,DynamicRequirementGroup dynamicRequirementGroup,
			CacRequirementGroup group, int groupIndex) {
		
		List<ResponseValueType> responseValueTypeList = new ArrayList<ResponseValueType>();		
			
		addRequirementValueOnResponse(fiscalCode,ccvRequirement, ESPDCriterion, responseValueTypeList,dynamicRequirementGroup, group, groupIndex);		
		
		return responseValueTypeList;
	}

	private void addRequirementValueOnResponse(String fiscalCode,CacCriterionRequirement ccvRequirement, ESPDCriterion ESPDCriterion,
			List<ResponseValueType> responseValueTypeList,DynamicRequirementGroup dynamicRequirementGroup, CacRequirementGroup group, int groupIndex) {
		if (ESPDCriterion == null) {
			return;
		}
		ResponseValueType ResponseValueType = new ResponseValueType();
		IDType idType = CommonUblFactory.buildIdType(fiscalCode);
		idType.setValue(UUID.randomUUID().toString());
		idType.setSchemeID("ISO/IEC 9834-8:2008");
		idType.setSchemeVersionID("4");
		ResponseValueType.setID(idType);
		CbcResponseType type =  ccvRequirement.getResponseType();		
		// values from the Maps of the unbounded groups are somehow(how?) converted to Strings
		Object value = readRequirementFirstValue(ccvRequirement, ESPDCriterion, dynamicRequirementGroup, group, groupIndex);
		if(value != null) {
			
			switch (type.getCode()) {
			case "INDICATOR":
				if (value != null) {				
					ResponseIndicatorType rit=new ResponseIndicatorType();
					rit.setValue((Boolean) value);
					ResponseValueType.setResponseIndicator(rit);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "DATE":
				if (/*group.isUnbounded() && */value instanceof String) {
					value = parseDateFromString((String) value);
				}				
				
				try {
					GregorianCalendar cal = new GregorianCalendar();
					Date date;
					
					//date = new SimpleDateFormat("yyyy-MM-dd").parse((String) value);
					
				    cal.setTime((Date) value);
					
					
					ResponseDateType rdt=new ResponseDateType();
				
					rdt.setValue( DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
					ResponseValueType.setResponseDate(rdt);
					responseValueTypeList.add(ResponseValueType);
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					log.error("Sie è verificato un errore nel metodo: addRequirementValueOnResponse", e);
				}
				
				break;			
			case "DESCRIPTION":			
				ResponseValueType.getDescription().add(UblRequirementFactory.buildDescriptionType((String) value));
				responseValueTypeList.add(ResponseValueType);
				break;
			case "URL":
				ResponseURIType rut=new ResponseURIType();
				rut.setValue((String) value);
				ResponseValueType.setResponseURI(rut);
				responseValueTypeList.add(ResponseValueType);
				break;
			case "QUANTITY":
				if(value != null) {					
					if (group.isUnbounded() && value instanceof String) {
						value = parseBigDecimalFromString((String) value);
					}
					if (value instanceof Long) {
						value=BigDecimal.valueOf((Long)value);
					}
					if (value instanceof Integer) {
						value=BigDecimal.valueOf((Integer)value);
					}
					if (value instanceof Double) {
						value=BigDecimal.valueOf((Double)value);
					}
					
					ResponseQuantityType rqt=new ResponseQuantityType();
					rqt.setValue((BigDecimal) value);
					ResponseValueType.setResponseQuantity(rqt);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "QUANTITY_YEAR":
				if(value != null) {	
					if (group.isUnbounded() && value instanceof String) {
						value = parseIntegerFromString((String) value);
					}
//					if (value instanceof Long) {
//						value=BigDecimal.valueOf((Long)value);
//					}		
					if (value instanceof Integer) {
						value=BigDecimal.valueOf((Integer)value);
					}
					ResponseQuantityType rqt2=new ResponseQuantityType();
					rqt2.setValue((BigDecimal) BigDecimal.valueOf(Long.valueOf(value.toString())));
					rqt2.setUnitCode("YEAR");
					ResponseValueType.setResponseQuantity(rqt2);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "QUANTITY_INTEGER":
				if(value != null) {	
					if (group.isUnbounded() && value instanceof String) {
						value = parseIntegerFromString((String) value);
					}
					if (value instanceof Long) {
						value=BigDecimal.valueOf((Long)value);
					}
					if (value instanceof Integer) {
						value=BigDecimal.valueOf((Integer)value);
					}
					if (value instanceof Double) {
						value=BigDecimal.valueOf((Double)value);
					}
					ResponseQuantityType rqt3=new ResponseQuantityType();
					rqt3.setValue((BigDecimal) value);
					ResponseValueType.setResponseQuantity(rqt3);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "AMOUNT":
				if(value != null) {	
					if (group.isUnbounded() && value instanceof String) {
						value = parseBigDecimalFromString((String) value);
					}
					if (value instanceof Integer) {
						value=BigDecimal.valueOf((Integer)value);
					}
					if (value instanceof Long) {
						value=BigDecimal.valueOf((Long)value);
					}
					if (value instanceof Double) {
						value=BigDecimal.valueOf((Double)value);
					}
					String currency = readRequirementSecondValue(ccvRequirement, ESPDCriterion,dynamicRequirementGroup, group, groupIndex);
					ResponseAmountType rat=new ResponseAmountType();
					rat.setValue((BigDecimal)value);					
					rat.setCurrencyID(currency);
					ResponseValueType.setResponseAmount(rat);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "CODE_COUNTRY":
				ResponseCodeType rct=new ResponseCodeType();
				String ct=(String)value;
				
				rct.setValue(ct);
				
				ResponseValueType.setResponseCode(rct);
				responseValueTypeList.add(ResponseValueType);
				break;
			case "PERCENTAGE":
				if(value != null) {	
					if (group.isUnbounded() && value instanceof String) {
						value = parseBigDecimalFromString((String) value);
					}
					if (value instanceof Long) {
						value=BigDecimal.valueOf((Long)value);
					}
					if (value instanceof Double) {
						value=BigDecimal.valueOf((Double)value);
					}
					ResponseQuantityType rqt4=new ResponseQuantityType();
					rqt4.setValue((BigDecimal) value);
					rqt4.setUnitCode("PERCENTAGE");
					ResponseValueType.setResponseQuantity(rqt4);
					responseValueTypeList.add(ResponseValueType);
				}
				break;							
			case "LOT_IDENTIFIER":				
				ResponseIDType rid=new ResponseIDType();
				if (value instanceof List<?>) {
					
					for (HashMap<String, String> s : (List<HashMap<String, String>>)value) {
						ResponseValueType rvt=new ResponseValueType();
						ResponseIDType rCode=new ResponseIDType();
						rCode.setSchemeAgencyID("#");
						rCode.setValue(s.get("lotId"));
						rvt.setResponseID(rCode);
						responseValueTypeList.add(rvt);
					}
					
					
					
				}else {
					
					rid.setValue((String)value);
					ResponseValueType.setResponseID(rid);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "ECONOMIC_OPERATOR_IDENTIFIER":
				ResponseIDType rct4=new ResponseIDType();
				if (value instanceof List<?>) {
					
				}
				rct4.setValue((String)value);
				rct4.setSchemeAgencyID("#");
				ResponseValueType.setResponseID(rct4);
				responseValueTypeList.add(ResponseValueType);
				break;
			case "CODE":			
				ResponseCodeType rct3=new ResponseCodeType();
				rct3.setValue((String)value);
				ResponseValueType.setResponseCode(rct3);
				responseValueTypeList.add(ResponseValueType);
				break;			
			case "QUAL_IDENTIFIER":
				ResponseValueType.getDescription().add(UblRequirementFactory.buildDescriptionType((String) value));
				responseValueTypeList.add(ResponseValueType);
				break;
			
			default:
				throw new IllegalArgumentException(String.format(
						"Could not save the requirement '%s' with id '%s' and expected response type '%s' on the ESPD Response.",
						ccvRequirement, ccvRequirement.getId(), type));
			}
		}
		
	}

	private BigDecimal parseBigDecimalFromString(String value) {
		if (isBlank(value)) {
			return null;
		}
		DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
		nf.setParseBigDecimal(true);
		return (BigDecimal) nf.parse(value, new ParsePosition(0));
	}

	private Integer parseIntegerFromString(String value) {
		if (isBlank(value)) {
			return null;
		}
		return Integer.valueOf(value);
	}

	private Date parseDateFromString(String value) {
		if (isBlank(value)) {
			return null;
		}
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error("Sie è verificato un errore nel metodo: parseBigDecimalFromString", e);
		}  
		return date;
	}

	private <T> T readRequirementFirstValue(CacCriterionRequirement requirement, ESPDCriterion ESPDCriterion,DynamicRequirementGroup dynamicRequirementGroup,
			CacRequirementGroup group, int groupIndex) {
		// most requirements are mapped to only one ESPD field
		return readRequirementValueAtPosition(requirement, ESPDCriterion,dynamicRequirementGroup, 0, group, groupIndex);
	}

	private <T> T readRequirementSecondValue(CacCriterionRequirement requirement, ESPDCriterion ESPDCriterion,DynamicRequirementGroup dynamicRequirementGroup,
			CacRequirementGroup group, int groupIndex) {
		// this method is needed by requirements of type AMOUNT which are mapped to two fields (amount and currency)
		return readRequirementValueAtPosition(requirement, ESPDCriterion,dynamicRequirementGroup, 1, group, groupIndex);
	}

	@SuppressWarnings("unchecked")
	private <T> T readRequirementValueAtPosition(CacCriterionRequirement requirement, ESPDCriterion espdCriterion,DynamicRequirementGroup dynamicRequirementGroup,
			int position, CacRequirementGroup group, int groupIndex) {
		if (requirement.getEspdCriterionFields().isEmpty() || requirement.getEspdCriterionFields().get(position) == null) {
			// there is one criterion which is not mapped to any field (3a6fefd4-f458-4d43-97fb-0725fce5dce2) financial ratio
			return null;
		}
	
		try {
			
			if(dynamicRequirementGroup == null) {
				try {
					return (T) PropertyUtils.getProperty(espdCriterion, requirement.getEspdCriterionFields().get(position));
				} catch (IntrospectionException e) {					
					log.error("Si è verificato un errore nel metodo: readRequirementValueAtPosition", e);
				}
			}else {
				return (T) dynamicRequirementGroup.get(requirement.getEspdCriterionFields().get(position));
			}
	/*		if (group.isUnbounded() || groupIndex >= 0) {
				List<DynamicRequirementGroup> unboundedGroups = null;
				try {
					
					unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				}
				catch (Exception e) {
					// TODO: handle exception
				}	
				if (unboundedGroups == null || unboundedGroups.isEmpty()) {
					return null;
				}
				
				{// Workaround for subgroup indicator //////////////////
					if("selfCleaningAnswer".equals(requirement.getEspdCriterionFields().get(position))) {
						return (T) unboundedGroups.get(groupIndex).getSubIndicatorAnswer();
					}
				}///////////////////////////////////////////////////////
				return (T) unboundedGroups.get(groupIndex).get(requirement.getEspdCriterionFields().get(position));

			}
			// all requirements except the ones representing an AMOUNT are mapped to a single ESPD field
			try {
				return (T) PropertyUtils.getProperty(espdCriterion, requirement.getEspdCriterionFields().get(position));
			} catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return null;
	}
	
	private static boolean isBlank(String s) {
		if (s == null || "".equals(s)) {
			return true;
		}
		return false;
	}




}
