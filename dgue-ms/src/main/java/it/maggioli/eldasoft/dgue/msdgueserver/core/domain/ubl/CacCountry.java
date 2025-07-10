package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CacCountry extends Serializable, CodeList {

    String getName();

    String getIso2Code();
    
    String getIsoType();
}
