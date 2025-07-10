package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;
import java.math.BigDecimal;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.PartyImpl;

/**
 * The contracting authority or contracting entity who is buying supplies,
 * services or public works using a tendering procedure as described in the
 * applicable directive (Directives 2016/07/EU).
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CacParty extends Serializable {

    String getName();
    
    String getVatNumber();

    String getAnotherNationalId();

    String getWebsite();

    String getEmail();

    String getStreet();

    String getPostalCode();

    String getCity();

    CacCountry getCountry();

    String getContactName();

    String getContactPhone();

    String getContactFax();

    String getContactEmail();

    /**
     * URL della sezione dell sito web della autorità contraente, che contiene
     * informazioni sulla stazione appaltante, le norme applicate al momento della
     * stipula del contratto, bandi pubblicati, avvisi preventivi e bandi di gara nonché
     * i relativi documenti di gara, avvisi di aggiudicazione del contratto, ecc.
     *
     * @return
     */
    String getProfileURI();
    
    String getRole();

    PartyImpl getServiceProvider();
    
    BigDecimal getEmployeeQuantity();
    
    BigDecimal getValueAmount();
}
