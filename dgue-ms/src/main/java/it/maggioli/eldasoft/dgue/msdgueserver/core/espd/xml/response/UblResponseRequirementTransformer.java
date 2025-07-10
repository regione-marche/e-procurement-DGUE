package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.logging.log4j.core.util.datetime.Format;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcResponseType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.CommonUblFactory;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblRequirementTypeTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceSuppliedType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseValueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseURIType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValidatedCriterionPropertyIDType;


@Slf4j
public class UblResponseRequirementTransformer extends UblRequirementTypeTemplate {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
		vcId.setSchemeAgencyName(fiscalCode);
		requirementType.setValidatedCriterionPropertyID(vcId);
		if(evidenceCode != null && !"".equals(evidenceCode)) {
			EvidenceSuppliedType evType=new EvidenceSuppliedType();
			IDType id=new IDType();
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
					// TODO Auto-generated catch block
					log.error("Si è verificato un errore nel metodo: buildRequirementTypeResponse", e);
				} 
			}else {
			
				requirementType.getResponseValue().addAll(buildResponse(fiscalCode,ccvRequirement, espdCriterion,dynamicRequirementGroup, requirementGroup, unboundedGroupIndex));
			}
		}
		
		return requirementType;
	}

	private List<ResponseValueType> buildResponse(String fiscalCode, CacCriterionRequirement ccvRequirement, ESPDCriterion ESPDCriterion,DynamicRequirementGroup dynamicRequirementGroup,
			CacRequirementGroup group, int groupIndex) {
		
		List<ResponseValueType> responseValueTypeList = new ArrayList<ResponseValueType>();
		ResponseValueType ResponseValueType = new ResponseValueType();
		IDType idType = CommonUblFactory.buildIdType(fiscalCode);
		idType.setValue(UUID.randomUUID().toString());
		idType.setSchemeID("ISO/IEC 9834-8:2008");
		idType.setSchemeVersionID("4");
		ResponseValueType.setID(idType);
			
		addRequirementValueOnResponse(ccvRequirement, ESPDCriterion, responseValueTypeList,dynamicRequirementGroup, group, groupIndex);
		

		return responseValueTypeList;
	}

	private void addRequirementValueOnResponse(CacCriterionRequirement ccvRequirement, ESPDCriterion ESPDCriterion,
			List<ResponseValueType> responseValueTypeList,DynamicRequirementGroup dynamicRequirementGroup, CacRequirementGroup group, int groupIndex) {
		if (ESPDCriterion == null) {
			return;
		}
		ResponseValueType ResponseValueType = new ResponseValueType();
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
					log.error("Si è verificato un errore nel metodo: addRequirementValueOnResponse", e);
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
//					if (value instanceof Long) {
//						value=BigDecimal.valueOf((Long)value);
//					}
					if (value instanceof Integer) {
						value=BigDecimal.valueOf((Integer)value);
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
				Country ct=(Country)value;
				
				rct.setValue(ct.getName());
				
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
					ResponseQuantityType rqt4=new ResponseQuantityType();
					rqt4.setValue((BigDecimal) value);
					ResponseValueType.setResponseQuantity(rqt4);
					responseValueTypeList.add(ResponseValueType);
				}
				break;							
			case "LOT_IDENTIFIER":
				ResponseCodeType rct2=new ResponseCodeType();
				if (value instanceof List<?>) {
					
					for (HashMap<String, String> s : (List<HashMap<String, String>>)value) {
						ResponseValueType rvt=new ResponseValueType();
						ResponseCodeType rCode=new ResponseCodeType();
						rCode.setValue(s.get("lotId"));
						rvt.setResponseCode(rCode);
						responseValueTypeList.add(rvt);
					}
					
					
					
				}else {
					
					rct2.setValue((String)value);
					ResponseValueType.setResponseCode(rct2);
					responseValueTypeList.add(ResponseValueType);
				}
				break;
			case "ECONOMIC_OPERATOR_IDENTIFIER":
				ResponseCodeType rct4=new ResponseCodeType();
				if (value instanceof List<?>) {
					
				}
				rct4.setValue((String)value);
				ResponseValueType.setResponseCode(rct4);
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
			log.error("Si è verificato un errore nel metodo: parseDateFromString", e);
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




	@Override
	protected TenderingCriterionPropertyType buildRequirementType(String fiscalCode,
			CacCriterionRequirement ccvRequirement, ESPDCriterion espdCriterion, CacRequirementGroup requirementGroup,
			DynamicRequirementGroup dynamicRequirementGroup, int unboundedGroupIndex, String evidenceCode,
			List<TenderingCriterionResponseType> tcrList) {
		// TODO Auto-generated method stub
		return null;
	}


}
