package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Function;

import io.micrometer.core.instrument.util.StringUtils;
import it.maggioli.eldasoft.dgue.msdgueserver.controllers.ESPDRequestController;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EconomicOperatorImpl;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EconomicOperatorRepresentative;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorRoleType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PersonType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PowerOfAttorneyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BirthDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BirthplaceNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ElectronicMailType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.FamilyNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.FirstNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.RoleCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TelephoneType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_2.IndicatorType;

/**
 * Builds an UBL {@link EconomicOperatorPartyType} that contains the information regarding the economic operator,
 * its representative and additional information.
 * <p/>
 * <p/>
 * Created by ratoico on 1/18/16 at 5:34 PM.
 */
@Slf4j
@Component
public class UblEconomicOperatorPartyTypeTransformer
		implements Function<EconomicOperatorImpl, EconomicOperatorPartyType> {

	@SuppressWarnings("java:S3749")
	private final UblPartyTypeTransformer partyTypeTransformer = new UblPartyTypeTransformer();

	@Override
	public EconomicOperatorPartyType apply(EconomicOperatorImpl input) {
		if (input == null) {
			return null;
		}

		EconomicOperatorPartyType eoPartyType = new EconomicOperatorPartyType();

		eoPartyType.setParty(partyTypeTransformer.apply(input));
		
		buildRepresentatives(input, eoPartyType);
					
		
		EconomicOperatorRoleType economicOperatorRoleType=new EconomicOperatorRoleType();
		RoleCodeType rct=new RoleCodeType();
		rct.setValue(input.getRole());
		rct.setListID("EORoleType");
		rct.setListAgencyID("EU-COM-GROW");
		rct.setListVersionID("2.1.1");
		rct.setListAgencyName("DG GROW (European Commission)");		
		economicOperatorRoleType.setRoleCode(rct);
		eoPartyType.setEconomicOperatorRole(economicOperatorRoleType);
		
		return eoPartyType;
	} 
	
	

	private void buildRepresentatives(EconomicOperatorImpl input, EconomicOperatorPartyType eoPartyType) {
		if (CollectionUtils.isEmpty(input.getRepresentatives())) {
			return;
		}
		
		
		
		for (EconomicOperatorRepresentative representative : input.getRepresentatives()) {
			if (representative == null) {
				continue;
			}
			
			eoPartyType.getParty().getPowerOfAttorney().add(buildPowerOfAttorney(representative));
			//eoPartyType.getParty().set;(buildRepresentative(representative));
		}
	}

	private EconomicOperatorPartyType buildRepresentative(EconomicOperatorRepresentative representative) {
		EconomicOperatorPartyType naturalPersonType = new EconomicOperatorPartyType();		
		EconomicOperatorRoleType economicOperatorRoleType=new EconomicOperatorRoleType();
		naturalPersonType.getParty().getPowerOfAttorney().add(buildPowerOfAttorney(representative));
		economicOperatorRoleType.setRoleCode(buildRepresentativeRole(representative));
		naturalPersonType.setEconomicOperatorRole(economicOperatorRoleType);		

		return naturalPersonType;
	}

	private RoleCodeType buildRepresentativeRole(EconomicOperatorRepresentative representative) {
		if (StringUtils.isBlank(representative.getPosition())) {
			return null;
		}

		RoleCodeType descriptionType = new RoleCodeType();
		descriptionType.setValue(representative.getPosition());
		return descriptionType;
	}

	private PowerOfAttorneyType buildPowerOfAttorney(EconomicOperatorRepresentative representative) {
		PowerOfAttorneyType attorneyType = new PowerOfAttorneyType();

		if (!isBlank(representative.getAdditionalInfo())) {
			DescriptionType descriptionType = new DescriptionType();
			descriptionType.setValue(representative.getAdditionalInfo());
			attorneyType.getDescription().add(descriptionType);
		}

		attorneyType.setAgentParty(buildAgentPartyType(representative));

		return attorneyType;
	}

	private PartyType buildAgentPartyType(EconomicOperatorRepresentative representative) {
		PartyType agentParty = new PartyType();

		agentParty.getPerson().add(buildPersonType(representative));

		return agentParty;
	}

	private PersonType buildPersonType(EconomicOperatorRepresentative representative) {
		PersonType personType = new PersonType();

		if (!isBlank(representative.getFirstName())) {
			FirstNameType firstName = new FirstNameType();
			firstName.setValue(trimToEmpty(representative.getFirstName()));
			personType.setFirstName(firstName);
		}
		if (!isBlank(representative.getLastName())) {
			FamilyNameType familyName = new FamilyNameType();
			familyName.setValue(trimToEmpty(representative.getLastName()));
			personType.setFamilyName(familyName);
		}
		if (!isBlank(representative.getFiscalCode())) {
			IDType id = new IDType();
			id.setValue(trimToEmpty(representative.getFiscalCode()));
			personType.setID(id);
		}
		if (representative.getDateOfBirth() != null) {
			BirthDateType birthDate = new BirthDateType();
			
				GregorianCalendar cal = new GregorianCalendar();
			    
			    cal.setTime(representative.getDateOfBirth());
			    XMLGregorianCalendar calendar;
				try {
					calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
					birthDate.setValue(calendar);
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					log.error("Sie è verificato un errore nel metodo: buildPersonType", e);
				}
			
			personType.setBirthDate(birthDate);
		}
		if (!isBlank(representative.getPlaceOfBirth())) {
			BirthplaceNameType birthplaceName = new BirthplaceNameType();
			birthplaceName.setValue(trimToEmpty(representative.getPlaceOfBirth()));
			personType.setBirthplaceName(birthplaceName);
		}

		personType.setResidenceAddress(buildPersonAddress(representative));
		personType.setContact(buildContact(representative));

		return personType;
	}

	private AddressType buildPersonAddress(EconomicOperatorRepresentative representative) {
		// TODO this code is the same as UblPartyTypeTransformer (we need an abstraction)
		AddressType addressType = new AddressType();
		addCountryInformation(representative, addressType);
		addCityInformation(representative, addressType);
		addStreetInformation(representative, addressType);
		addPostboxInformation(representative, addressType);
		return addressType;
	}

	private void addCountryInformation(EconomicOperatorRepresentative representative, AddressType addressType) {
		if (representative.getCountry() == null) {
			return;
		}

		addressType.setCountry(CommonUblFactory.buildCountryType(representative.getCountry()));
	}

	private void addCityInformation(EconomicOperatorRepresentative representative, AddressType addressType) {
		if (isBlank(representative.getCity())) {
			return;
		}

		CityNameType cityName = new CityNameType();
		cityName.setValue(trimToEmpty(representative.getCity()));
		addressType.setCityName(cityName);
	}

	private void addStreetInformation(EconomicOperatorRepresentative representative, AddressType addressType) {
		if (isBlank(representative.getStreet())) {
			return;
		}

		StreetNameType streetName = new StreetNameType();
		streetName.setValue(trimToEmpty(representative.getStreet()));
		addressType.setStreetName(streetName);
	}

	private void addPostboxInformation(EconomicOperatorRepresentative representative, AddressType addressType) {
		if (isBlank(representative.getPostalCode())) {
			return;
		}

		PostalZoneType postalZoneType = new PostalZoneType();
		postalZoneType.setValue(trimToEmpty(representative.getPostalCode()));
		addressType.setPostalZone(postalZoneType);

	}

	private ContactType buildContact(EconomicOperatorRepresentative representative) {
		// TODO this code is the same as UblPartyTypeTransformer (we need an abstraction)
		ContactType contactType = new ContactType();

		addContactEmailInformation(representative, contactType);
		addContactTelephoneInformation(representative, contactType);

		return contactType;
	}

	private void addContactEmailInformation(EconomicOperatorRepresentative representative, ContactType contactType) {
		if (isBlank(representative.getEmail())) {
			return;
		}

		ElectronicMailType electronicMail = new ElectronicMailType();
		electronicMail.setValue(trimToEmpty(representative.getEmail()));
		contactType.setElectronicMail(electronicMail);
	}

	private void addContactTelephoneInformation(EconomicOperatorRepresentative representative,
			ContactType contactType) {
		if (isBlank(representative.getPhone())) {
			return;
		}

		TelephoneType telephone = new TelephoneType();
		telephone.setValue(trimToEmpty(representative.getPhone()));
		contactType.setTelephone(telephone);
	}

	private IndicatorType buildSmeIndicator(Boolean isSme) {
		IndicatorType smeIndicator = new IndicatorType();
		smeIndicator.setValue(Boolean.TRUE.equals(isSme));
		return smeIndicator;
	}
	
	private boolean isBlank(String s) {
    	if(s == null || "".equals(s)) {
    		return true;
    	}
    	return false;
    }
	
	public static String trimToEmpty(final String str) {
		return str == null ? "" : str.trim();
	}
}
