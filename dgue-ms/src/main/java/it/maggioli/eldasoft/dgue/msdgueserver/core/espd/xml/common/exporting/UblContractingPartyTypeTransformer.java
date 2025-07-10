package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import com.google.common.base.Function;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacParty;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractingPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BuyerProfileURIType;
import org.springframework.stereotype.Component;

/**
 * Transforms the information coming from a {@link CacParty} into a {@link ContractingPartyType} object.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 04, 2020
 */
@Component
public class UblContractingPartyTypeTransformer implements Function<CacParty, ContractingPartyType> {

	@SuppressWarnings("java:S3749")
    private final UblPartyTypeTransformer partyTypeTransformer = new UblPartyTypeTransformer();

    @Override
    public ContractingPartyType apply(CacParty party) {
        if (party == null) {
            return null;
        }
        ContractingPartyType contractingPartyType = new ContractingPartyType();

        contractingPartyType.setParty(partyTypeTransformer.apply(party));
        addBuyerProfileURIInformation(party, contractingPartyType);

        return contractingPartyType;
    }

    private void addBuyerProfileURIInformation(final CacParty cacParty, final ContractingPartyType contractingPartyType) {
        final BuyerProfileURIType buyerProfileURIType = new BuyerProfileURIType();
        buyerProfileURIType.setValue(cacParty.getProfileURI());
        contractingPartyType.setBuyerProfileURI(buyerProfileURIType);
    }
}
