package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;



import it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EconomicOperatorImpl;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EconomicOperatorRepresentative;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.PartyImpl;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EconomicOperatorPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PersonType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PowerOfAttorneyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BirthDateType;

/**
 * Created by ratoico on 1/19/16 at 10:27 AM.
 */
@Component
@Slf4j
public class EconomicOperatorImplTransformer {

    private final PartyImplTransformer partyImplTransformer;

    @Autowired
    EconomicOperatorImplTransformer(PartyImplTransformer partyImplTransformer) {
        this.partyImplTransformer = partyImplTransformer;
    }

    public EconomicOperatorImpl buildEconomicOperator(EconomicOperatorPartyType input) {
        EconomicOperatorImpl economicOperator = new EconomicOperatorImpl();

        if (input.getParty() != null) {
            PartyImpl party = partyImplTransformer.apply(input.getParty());
            economicOperator.copyProperties(party);
        }

//        if (input.getSMEIndicator() != null) {
//            economicOperator.setIsSmallSizedEnterprise(input.getSMEIndicator().isValue());
//        }
        economicOperator.setRole(input.getEconomicOperatorRole().getRoleCode().getValue());
        economicOperator.setRepresentatives(buildRepresentatives(input));

        return economicOperator;
    }

	private List<EconomicOperatorRepresentative> buildRepresentatives(EconomicOperatorPartyType input) {
		if (CollectionUtils.isEmpty(input.getParty().getPowerOfAttorney())) {
			return Collections.emptyList();
		}

		List<EconomicOperatorRepresentative> representatives = new ArrayList<EconomicOperatorRepresentative>();
		for (PowerOfAttorneyType powerOfAttorneyType : input.getParty().getPowerOfAttorney()) {
			
			representatives.add(addPowerOfAttorneyInformation(powerOfAttorneyType));
		}

		return representatives;
	}


    private EconomicOperatorRepresentative addPowerOfAttorneyInformation(PowerOfAttorneyType powerOfAttorney) {
    	EconomicOperatorRepresentative representative = new EconomicOperatorRepresentative();
        if (powerOfAttorney == null) {
            return null;
        }

        if (!powerOfAttorney.getDescription().isEmpty()) {
            representative.setAdditionalInfo(StringUtils.trimToEmpty(powerOfAttorney.getDescription().get(0).getValue()));
        }

        addAgentPartyInformation(representative, powerOfAttorney);
        return representative;
    }

    private void addAgentPartyInformation(EconomicOperatorRepresentative representative,
            PowerOfAttorneyType powerOfAttorney) {
        if (powerOfAttorney.getAgentParty() == null || CollectionUtils
                .isEmpty(powerOfAttorney.getAgentParty().getPerson())) {
            return;
        }

        PersonType personType = powerOfAttorney.getAgentParty().getPerson().get(0);

        if (personType.getFirstName() != null) {
            representative.setFirstName(StringUtils.trimToEmpty(personType.getFirstName().getValue()));
        }
        if (personType.getID() != null) {
            representative.setFiscalCode(StringUtils.trimToEmpty(personType.getID().getValue()));
        }
        if (personType.getFamilyName() != null) {
            representative.setLastName(StringUtils.trimToEmpty(personType.getFamilyName().getValue()));
        }
        if (personType.getBirthDate() != null && personType.getBirthDate().getValue() != null) {
            representative.setDateOfBirth(personType.getBirthDate().getValue().toGregorianCalendar().getTime());
           
        }
        if (personType.getBirthplaceName() != null) {
            representative.setPlaceOfBirth(StringUtils.trimToEmpty(personType.getBirthplaceName().getValue()));
        }

        addResidenceAddressInformation(representative, personType.getResidenceAddress());
        addContactInformation(representative, personType.getContact());

    }

    private void addResidenceAddressInformation(EconomicOperatorRepresentative representative,
            AddressType residenceAddress) {
        if (residenceAddress == null) {
            return;
        }

        //For backwards computability, we read both PostBox and PostalZone - preferring PostalZone when it is not null

        if (residenceAddress.getPostbox() != null) {
            representative.setPostalCode(StringUtils.trimToEmpty(residenceAddress.getPostbox().getValue()));
        }

        if (residenceAddress.getPostalZone() != null) {
            representative.setPostalCode(StringUtils.trimToEmpty(residenceAddress.getPostalZone().getValue()));
        }


        if (residenceAddress.getStreetName() != null) {
            representative.setStreet(StringUtils.trimToEmpty(residenceAddress.getStreetName().getValue()));
        }
        if (residenceAddress.getCityName() != null) {
            representative.setCity(StringUtils.trimToEmpty(residenceAddress.getCityName().getValue()));
        }
        if (residenceAddress.getCountry() != null && residenceAddress.getCountry().getIdentificationCode() != null) {
            representative.setCountry(partyImplTransformer.readCountry(residenceAddress.getCountry()));
        }
    }

    private void addContactInformation(EconomicOperatorRepresentative representative, ContactType contact) {
        if (contact == null) {
            return;
        }
        if (contact.getElectronicMail() != null) {
            representative.setEmail(StringUtils.trimToEmpty(contact.getElectronicMail().getValue()));
        }
        if (contact.getTelephone() != null) {
            representative.setPhone(StringUtils.trimToEmpty(contact.getTelephone().getValue()));
        }
    }
}
