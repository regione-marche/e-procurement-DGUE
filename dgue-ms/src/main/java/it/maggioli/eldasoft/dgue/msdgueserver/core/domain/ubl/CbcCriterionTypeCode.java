package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;

/**
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CbcCriterionTypeCode extends Serializable {

    /**
     * ESPD specific type used to build different types of criteria.
     *
     * @return
     */
    String getEspdType();

    /**
     * Code used in the criterion taxonomies.
     *
     * @return
     */
    String getCode();

}
