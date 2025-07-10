package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.PartyImpl;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CountryType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.MarshallingConstants;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;

/**
 * Transforms a UBL {@link PartyType} into an internal ESPD Party implementation as {@link PartyImpl}.
 * <p/>
 * Created by ratoico on 11/25/15.
 */
@Component
public class PartyImplTransformer {

	@SuppressWarnings("java:S3749")
	private final PartyIdentificationTypePredicate vatNumberPredicate = new PartyIdentificationTypePredicate(
			MarshallingConstants.VAT_NUMBER_SCHEME_ID);
	@SuppressWarnings("java:S3749")
	private final PartyIdentificationTypePredicate nationalNumberPredicate = new PartyIdentificationTypePredicate(
			MarshallingConstants.NATIONAL_NUMBER_SCHEME_ID);

	public PartyImpl apply(PartyType input) {
		PartyImpl authority = new PartyImpl();
		
		addName(input, authority);
		addWebsite(input, authority);
		addEndPointId(input, authority);
		addVatNumber(input, authority);
		addAddressInformation(input, authority);
		addContactInformation(input, authority);
		
		PartyImpl serviceProvider = new PartyImpl();
		if(input.getServiceProviderParty() != null && input.getServiceProviderParty().size() > 0) {
			
			addName(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			addWebsite(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			addEndPointId(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			addVatNumber(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			addAddressInformation(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			addContactInformation(input.getServiceProviderParty().get(0).getParty(), serviceProvider);
			authority.setServiceProvider(serviceProvider);
		}
		
		return authority;
	}

	private void addName(PartyType input, PartyImpl party) {
		if (input.getPartyName() == null) {
			return;
		}
		if (input.getPartyName().get(0).getName() == null) {
			return;
		}

		party.setName(StringUtils.trimToEmpty(input.getPartyName().get(0).getName().getValue()));
	}
	
	private void addEndPointId(PartyType input, PartyImpl party) {
		if (input.getEndpointID() == null) {
			return;
		}
		if (input.getEndpointID().getValue() == null) {
			return;
		}

		party.setEmail(StringUtils.trimToEmpty(input.getEndpointID().getValue()));
	}

	private void addWebsite(PartyType input, PartyImpl party) {
		if (input.getWebsiteURI() == null) {
			return;
		}

		party.setWebsite(StringUtils.trimToEmpty(input.getWebsiteURI().getValue()));
	}

	private void addVatNumber(PartyType input, PartyImpl party) {
		if (input.getPartyIdentification() == null) {
			return;
		}

		// this code is left here for compatibility with versions prior to 2016.08
		PartyIdentificationType vat1Type = input.getPartyIdentification().get(0);
		if (vat1Type.getID() != null  && (!"VAT".equals(vat1Type.getID().getSchemeID()) && !"NATIONAL".equals(vat1Type.getID().getSchemeID()))) {
			party.setVatNumber(StringUtils.trimToEmpty(vat1Type.getID().getValue()));
		}
		if (vat1Type.getID() != null && "VAT".equals(vat1Type.getID().getSchemeID())) {
			party.setVatNumber(StringUtils.trimToEmpty(vat1Type.getID().getValue()));
		}
		if (vat1Type.getID() != null && "NATIONAL".equals(vat1Type.getID().getSchemeID())) {
			party.setAnotherNationalId(StringUtils.trimToEmpty(vat1Type.getID().getValue()));
		}
		if (input.getPartyIdentification().size() > 1) {
			PartyIdentificationType vat2Type = input.getPartyIdentification().get(1);
			if (vat2Type.getID() != null  && (!"VAT".equals(vat1Type.getID().getSchemeID()) && !"NATIONAL".equals(vat1Type.getID().getSchemeID()))) {
				party.setAnotherNationalId(StringUtils.trimToEmpty(vat2Type.getID().getValue()));
			}
			if (vat2Type.getID() != null && "VAT".equals(vat2Type.getID().getSchemeID())) {
				party.setVatNumber(StringUtils.trimToEmpty(vat2Type.getID().getValue()));
			}
			if (vat2Type.getID() != null && "NATIONAL".equals(vat2Type.getID().getSchemeID())) {
				party.setAnotherNationalId(StringUtils.trimToEmpty(vat2Type.getID().getValue()));
			}
		}

		// this code uses 'schemeID' to distinguish between VAT number and national number
		String vatNumber = getIdentificationNumber(input, vatNumberPredicate);
		String nationalNumber = getIdentificationNumber(input, nationalNumberPredicate);

		// if either of these is not blank it means we are using the model according to version 2016.08
		if (StringUtils.isNotBlank(vatNumber) || StringUtils.isNotBlank(nationalNumber)) {
			party.setVatNumber(StringUtils.trimToEmpty(vatNumber));
			party.setAnotherNationalId(StringUtils.trimToEmpty(nationalNumber));
		}
	} 

	private String getIdentificationNumber(PartyType input, PartyIdentificationTypePredicate typePredicate) {
		Collection<PartyIdentificationType> identificationTypes = Collections2
				.filter(input.getPartyIdentification(), typePredicate);
		if (identificationTypes != null && identificationTypes.size() > 0) {
			PartyIdentificationType identificationType = identificationTypes.iterator().next();
			if (identificationType.getID() != null) {
				return StringUtils.trimToEmpty(identificationType.getID().getValue());
			}
		}
		return null;
	}

	private void addAddressInformation(PartyType input, PartyImpl party) {
		if (input.getPostalAddress() == null) {
			return;
		}

		addStreetName(input.getPostalAddress(), party);
		addPostbox(input.getPostalAddress(), party);
		addCity(input.getPostalAddress(), party);
		addCountry(input.getPostalAddress(), party);
	}

	private void addStreetName(AddressType addressType, PartyImpl party) {
		if (addressType.getStreetName() == null) {
			return;
		}

		party.setStreet(StringUtils.trimToEmpty(addressType.getStreetName().getValue()));
	}

	private void addPostbox(AddressType addressType, PartyImpl party) {

		//For backwards computability, we read both PostBox and PostalZone - preferring PostalZone when it is not null

		if (addressType.getPostbox() != null) {
			party.setPostalCode(StringUtils.trimToEmpty(addressType.getPostbox().getValue()));
		}

		if (addressType.getPostalZone() != null) {
			party.setPostalCode(StringUtils.trimToEmpty(addressType.getPostalZone().getValue()));
		}

	}

	private void addCity(AddressType addressType, PartyImpl party) {
		if (addressType.getCityName() == null) {
			return;
		}
		
		party.setCity(StringUtils.trimToEmpty(addressType.getCityName().getValue()));
	}

	private void addCountry(AddressType addressType, PartyImpl party) {
		if (addressType.getCountry() == null || addressType.getCountry().getIdentificationCode() == null) {
			return;
		}

		party.setCountry(readCountry(addressType.getCountry()));
	}

	private void addContactInformation(PartyType input, PartyImpl party) {
		if (input.getContact() == null) {
			return;
		}

		addContactName(input.getContact(), party);
		addContactPhone(input.getContact(), party);
		addContactEmail(input.getContact(), party);
		addContactFax(input.getContact(), party);
	}

	private void addContactName(ContactType contactType, PartyImpl party) {
		if (contactType.getName() == null) {
			return;
		}

		party.setContactName(StringUtils.trimToEmpty(contactType.getName().getValue()));
	}

	private void addContactPhone(ContactType contactType, PartyImpl party) {
		if (contactType.getTelephone() == null) {
			return;
		}

		party.setContactPhone(StringUtils.trimToEmpty(contactType.getTelephone().getValue()));
	}

	private void addContactEmail(ContactType contactType, PartyImpl party) {
		if (contactType.getElectronicMail() == null) {
			return;
		}

		party.setContactEmail(StringUtils.trimToEmpty(contactType.getElectronicMail().getValue()));
	}
	
	private void addContactFax(ContactType contactType, PartyImpl party) {
		if (contactType.getTelefax() == null) {
			return;
		}

		party.setContactFax(StringUtils.trimToEmpty(contactType.getTelefax().getValue()));
	}

	Country readCountry(oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType countryType) {
		if (countryType == null || countryType.getIdentificationCode() == null) {
			return null;
		}

		String countryCode = StringUtils.trimToEmpty(countryType.getIdentificationCode().getValue());
		if (usesIso3Code(countryType) && countryCode.length() >= 2) {
			return Country.findByIso2Code(countryCode.substring(0, 2));
		}
		return Country.findByIso2Code(countryCode);
	}
	
	private boolean usesIso3Code(
			oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType countryType) {
		return CountryType.ISO_3166_2.getIsoType().equalsIgnoreCase(countryType.getIdentificationCode().getListName());
	}

	/**
	 * Identify a {@link PartyIdentificationType} based on the 'schemeID' attribute of its 'ID' element.
	 */
	private static class PartyIdentificationTypePredicate implements Predicate<PartyIdentificationType> {

		private final String partyType;

		private PartyIdentificationTypePredicate(String partyType) {
			this.partyType = partyType;
		}

		@Override
		public boolean apply(PartyIdentificationType input) {
			if (input == null || input.getID() == null) {
				return false;
			}
			return partyType.equals(input.getID().getSchemeID());
		}
	}
}
