package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Getter
public enum CountryType {

    ISO_3166_1("ISO 3166-1"),
    ISO_3166_2("ISO 3166-2");
    
    private String isoType;
    
    CountryType(String isoType) {
    	this.isoType = isoType;
    }
    
}
