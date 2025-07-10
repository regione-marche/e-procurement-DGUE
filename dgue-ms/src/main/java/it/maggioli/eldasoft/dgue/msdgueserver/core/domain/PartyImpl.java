package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacParty;
import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
@Data
@Valid
public class PartyImpl implements CacParty {

    @NotEmpty
    private String name;
    @NotEmpty
    private String vatNumber;

    private String anotherNationalId;//If no VAT-number is applicable, please indicate another national identification number, if required and applicable

    private String website;

    private String email;

    private String street;

    private String postalCode;

    private String city;

    @NotNull
    private Country country;

    private String contactName;

    private String contactEmail;

    private String contactPhone;

    private String contactFax;

    private String profileURI;

    private PartyImpl serviceProvider;
    
    private String role;
    
    private BigDecimal employeeQuantity;
    
    private BigDecimal valueAmount;
    
    private String IndustryClassificationCode;
    
    
    
    
}
