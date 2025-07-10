package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.maggioli.eldasoft.dgue.msdgueserver.controllers.ESPDRequestController;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.CommonUblFactory;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PercentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.QuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.URIType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_2.IndicatorType;

@Slf4j
public final class UblRequirementFactory {

	private UblRequirementFactory() {

	}

	public static PeriodType buildPeriodType(String periodLength) {
		if (isBlank(periodLength)) {
			return null;
		}
		PeriodType periodType = new PeriodType();
		periodType.getDescription().add(buildDescriptionType(periodLength));
		return periodType;
	}

	public static TypeCodeType buildCodeType(String code) {
		if (isBlank(code)) {
			return null;
		}
		TypeCodeType typeCodeType = new TypeCodeType();
		typeCodeType.setValue(code);
		return typeCodeType;
	}

	public static DescriptionType buildDescriptionType(String description) {
		if (isBlank(description)) {
			// we don't want empty Description elements
			return null;
		}
		DescriptionType descriptionType = new DescriptionType();
		descriptionType.setValue(description);
		return descriptionType;
	}

	public static QuantityType buildYearType(Integer year) {
		if (year == null) {
			return null;
		}
		QuantityType quantityType = new QuantityType();
		quantityType.setValue(BigDecimal.valueOf(year));
		quantityType.setUnitCode("YEAR");
		return quantityType;
	}

	public static QuantityType buildQuantityType(BigDecimal quantity) {
		if (quantity == null) {
			return null;
		}
		QuantityType quantityType = new QuantityType();
		quantityType.setValue(quantity);
		return quantityType;
	}

	public static QuantityType buildQuantityIntegerType(Integer number) {
		if (number == null) {
			return null;
		}
		QuantityType quantityType = new QuantityType();
		quantityType.setValue(BigDecimal.valueOf(number));
		quantityType.setUnitCode("NUMBER");
		return quantityType;
	}

	public static AmountType buildAmountType(BigDecimal amount, String currency) {
		if (amount == null) {
			return null;
		}
		AmountType amountType = new AmountType();
		amountType.setValue(amount);
		amountType.setCurrencyID(currency);
		return amountType;
	}

	public static TypeCodeType buildCountryType(Country country) {
		if (country == null) {
			return null;
		}
		TypeCodeType typeCodeType = new TypeCodeType();
		typeCodeType.setValue(country.getIso2Code());
		typeCodeType.setListAgencyID("ISO");
		typeCodeType.setListID(country.getIsoType());
		typeCodeType.setListVersionID("1.0");
		return typeCodeType;
	}

	public static DateType buildDateType(Date date) {
		if (date == null) {
			return null;
		}
		DateType dateType = new DateType();
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar date2 = null;
		try {
			date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			log.error("Si è verificato un errore nel metodo: buildDateType", e);
		}
		dateType.setValue(date2);
		return dateType;
	}

	public static PercentType buildPercentType(BigDecimal percentage) {
		if (percentage == null) {
			return null;
		}
		PercentType percentType = new PercentType();
		percentType.setValue(percentage);
		return percentType;
	}

	public static IndicatorType buildIndicatorType(boolean value) {
		IndicatorType indicatorType = new IndicatorType();
		indicatorType.setValue(value);
		return indicatorType;
	}

	public static EvidenceType buildEvidenceType(String url) {
		if (isBlank(url)) {
			return null;
		}
		EvidenceType evidenceType = new EvidenceType();
		DocumentReferenceType documentReferenceType = new DocumentReferenceType();
		AttachmentType attachmentType = new AttachmentType();
		ExternalReferenceType externalReferenceType = new ExternalReferenceType();
		attachmentType.setExternalReference(externalReferenceType);
		URIType uriType = new URIType();
		uriType.setValue(url);
		// id is mandatory for EvidenceDocumentReference
		IDType idType = CommonUblFactory.buildIdType("ISO/IEC 9834-8:2008 - 4UUID");
		idType.setValue(UUID.randomUUID().toString());
		documentReferenceType.setID(idType);
		externalReferenceType.setURI(uriType);
		documentReferenceType.setAttachment(attachmentType);
		evidenceType.getDocumentReference().add(documentReferenceType);
		return evidenceType;
	}
	
	private static boolean isBlank(String s) {
		if (s == null || "".equals(s)) {
			return true;
		}
		return false;
	}

}
