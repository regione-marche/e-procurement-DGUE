package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isBlank;
import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isNotBlank;
import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.trimToEmpty;

import com.google.common.base.Function;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacParty;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContactType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ServiceProviderPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ElectronicMailType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndpointIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TelefaxType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TelephoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.WebsiteURIType;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 04, 2020
 */
final class UblPartyTypeTransformer implements Function<CacParty, PartyType> {

    @Override
    public PartyType apply(final CacParty input) {
        return apply(input, "IT:CF");
    }

    /**
     *
     * @param input
     * @param identificationSchemeId
     * @return
     */
    private PartyType apply(final CacParty input, final String identificationSchemeId) {
        final PartyType partyType = new PartyType();

        
        addPartyIdInformation(input, partyType, identificationSchemeId);              
        addPartyWebsiteInformation(input, partyType);
        addPartyEndpointInformation(input, partyType);
        addPartyNameInformation(input, partyType);
        addAddressInformation(input, partyType);
        addContactInformation(input, partyType);
        
        addServiceProviderInformation(input.getServiceProvider(), partyType);

        return partyType;
    }


    /**
     * Imposta l'identificatore nazionale della stazione appaltante
     *
     * @param schemeId il valore per l'attributo schemeId per l'oggetto IDType
     * @param party
     * @param partyType
     */
    private void addPartyIdInformation(final CacParty party, final PartyType partyType, final String schemeId) {
        boolean completed = false;
        if (isNotBlank(party.getVatNumber())) {
            partyType.getPartyIdentification().add(buildPartyIdentificationType("VAT", party.getVatNumber()));
            completed = true;
        }
        if (/*!completed && */isNotBlank(party.getAnotherNationalId())) {
            partyType.getPartyIdentification().add(buildPartyIdentificationType("NATIONAL", party.getAnotherNationalId()));
            completed = true;
        }

        if (!completed) {
            throw new IllegalArgumentException("Field vatNumber or anotherNationalId must be not null");
        }
    }
       

    /**
     * Imposta il sito web della stazione appaltante
     *
     * @param party
     * @param partyType
     */
    private void addPartyWebsiteInformation(final CacParty party, final PartyType partyType) {
        if (isBlank(party.getWebsite())) {
            return;
        }

        WebsiteURIType websiteURI = new WebsiteURIType();
        websiteURI.setValue(trimToEmpty(party.getWebsite()));
        partyType.setWebsiteURI(websiteURI);
    }

    /**
     * Imposta l'indirizzo elettronico della stazione appaltante
     *
     * @param party
     * @param partyType
     */
    private void addPartyEndpointInformation(final CacParty party, final PartyType partyType) {
        if (isBlank(party.getEmail())) {
            return;
        }

        final EndpointIDType endpointIDType = new EndpointIDType();
        endpointIDType.setSchemeID("#");
        endpointIDType.setSchemeAgencyID("#");
        endpointIDType.setValue(party.getEmail());
        partyType.setEndpointID(endpointIDType);
    }

    /**
     * Imposta la Ragione Sociale della stazione appaltante
     *
     * @param party
     * @param partyType
     */
    private void addPartyNameInformation(final CacParty party, final PartyType partyType) {
        if (isBlank(party.getName())) {
            throw new IllegalArgumentException("Field name must be not null");
        }

        final PartyNameType partyNameType = new PartyNameType();
        final NameType nameType = new NameType();
        nameType.setValue(trimToEmpty(party.getName()));
        partyNameType.setName(nameType);
        partyType.getPartyName().add(partyNameType);
    }

    /**
     * Crea l'identificatore nazionale della stazione appaltante (es. Partita IVA)
     *
     * @param schemeId
     * @param regNumber partita iva o codice fiscale della SA oppure un'altro identificativo nazionale
     * @return Il corrispondente elemento UBL
     */
    private PartyIdentificationType buildPartyIdentificationType(final String schemeId, final String regNumber) {
        final PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
        final IDType id = CommonUblFactory.buildIdType("AdE");
        id.setSchemeID(schemeId);
        id.setValue(trimToEmpty(regNumber));
        partyIdentificationType.setID(id);
        return partyIdentificationType;
    }


    /**
     * Imposta l'indirizzo della sede legale della Stazione Appaltante
     *
     * @param party
     * @param partyType
     */
    private void addAddressInformation(final CacParty party, final PartyType partyType) {
        final AddressType addressType = new AddressType();
        if (isBlank(party.getStreet()) && isBlank(party.getCity()) && isBlank(party.getPostalCode()) && party.getCountry() == null) {
            return;
        }
        addStreetInformation(party, addressType);
        addCityInformation(party, addressType);
        addPostboxInformation(party, addressType);
        addCountryInformation(party, addressType);

        partyType.setPostalAddress(addressType);
    }

    private void addStreetInformation(final CacParty party, final AddressType addressType) {
        if (isBlank(party.getStreet())) {
            return;
        }

        final StreetNameType streetName = new StreetNameType();
        streetName.setValue(trimToEmpty(party.getStreet()));
        addressType.setStreetName(streetName);

        final AddressLineType addressLineType = new AddressLineType();
        final LineType lineType = new LineType();
        lineType.setValue("1");
        addressLineType.setLine(lineType);
        addressType.getAddressLine().add(addressLineType);
    }

    private void addCityInformation(final CacParty party, final AddressType addressType) {
        if (isBlank(party.getCity())) {
            return;
        }

        final CityNameType cityName = new CityNameType();
        cityName.setValue(trimToEmpty(party.getCity()));
        addressType.setCityName(cityName);
    }

    private void addPostboxInformation(final CacParty party, final AddressType addressType) {
        if (isBlank(party.getPostalCode())) {
            return;
        }

        final PostalZoneType postalZoneType = new PostalZoneType();
        postalZoneType.setValue(trimToEmpty(party.getPostalCode()));
        addressType.setPostalZone(postalZoneType);
    }

    private void addCountryInformation(final CacParty party, final AddressType addressType) {
        if (party.getCountry() == null) {
            throw new IllegalArgumentException("Field Country must be not null");
        }

        addressType.setCountry(CommonUblFactory.buildCountryType(party.getCountry()));
    }

    /**
     * Utilizzato per fornire informazioni del contatto sia esso gruppo generico o
     * una persona
     *
     * @param party
     * @param partyType
     */
    private void addContactInformation(final CacParty party, final PartyType partyType) {
        final ContactType contactType = new ContactType();

        addContactNameInformation(party, contactType);
        addContactTelephoneInformation(party, contactType);
        addContactFaxInformation(party, contactType);
        addContactEmailInformation(party, contactType);

        partyType.setContact(contactType);
    }

    private void addContactNameInformation(final CacParty party, final ContactType contactType) {
        if (isBlank(party.getContactName())) {
            return;
        }

        final NameType name = new NameType();
        name.setValue(trimToEmpty(party.getContactName()));
        contactType.setName(name);
    }

    private void addContactTelephoneInformation(final CacParty party, final ContactType contactType) {
        if (isBlank(party.getContactPhone())) {
            return;
        }

        final TelephoneType telephone = new TelephoneType();
        telephone.setValue(trimToEmpty(party.getContactPhone()));
        contactType.setTelephone(telephone);
    }

    private void addContactFaxInformation(final CacParty party, final ContactType contactType) {
        if (isBlank(party.getContactFax())) {
            return;
        }

        final TelefaxType fax = new TelefaxType();
        fax.setValue(trimToEmpty(party.getContactFax()));
        contactType.setTelefax(fax);
    }

    private void addContactEmailInformation(final CacParty party, final ContactType contactType) {
        if (isBlank(party.getContactEmail())) {
            return;
        }

        final ElectronicMailType electronicMail = new ElectronicMailType();
        electronicMail.setValue(trimToEmpty(party.getContactEmail()));
        contactType.setElectronicMail(electronicMail);
    }

    /**
     *
     * @param party
     * @param partyType
     */
    private void addServiceProviderInformation(final CacParty party, final PartyType partyType) {
        if (party == null) {
            return;
        }
        final ServiceProviderPartyType serviceProviderPartyType = new ServiceProviderPartyType();
        serviceProviderPartyType.setParty(this.apply(party, "IT:VAT"));
        partyType.getServiceProviderParty().add(serviceProviderPartyType);
    }
}
