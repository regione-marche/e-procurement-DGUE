package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacParty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EconomicOperatorImpl extends PartyImpl {
	
	private Boolean isSmallSizedEnterprise = Boolean.FALSE; //Is the economic operator a Micro, a Small or a Medium-Sized Enterprise ?

    private List<EconomicOperatorRepresentative> representatives;
    
    public void setRepresentatives(List<EconomicOperatorRepresentative> representatives) {
    	this.representatives = representatives;
    }
    
    public void copyProperties(CacParty fromParty) {
        setName(fromParty.getName());
        setWebsite(fromParty.getWebsite());
        setVatNumber(fromParty.getVatNumber());
        setAnotherNationalId(fromParty.getAnotherNationalId());
        setStreet(fromParty.getStreet());
        setPostalCode(fromParty.getPostalCode());
        setCity(fromParty.getCity());
        setCountry((Country) fromParty.getCountry());
        setContactName(fromParty.getContactName());
        setContactPhone(fromParty.getContactPhone());
        setContactEmail(fromParty.getContactEmail());
    }

}
