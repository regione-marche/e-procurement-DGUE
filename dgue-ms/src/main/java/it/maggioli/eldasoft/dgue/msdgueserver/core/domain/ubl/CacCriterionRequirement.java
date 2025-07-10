package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CacCriterionRequirement extends Serializable {

    String getId();

    String getName();

    String getDescription();

	CbcExpectedResponse getExpectedResponse();

    CbcResponseType getResponseType();
    
    String getDescriptionSearch();

	/**
	 * The fields on the parent {@link it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion} which are mapped to the
	 * requirement. Usually, a requirement is mapped to one field only, but requirements of type AMOUNT are mapped to
	 * an 'amount' and 'currency' fields.
	 * @return
	 */
    List<String> getEspdCriterionFields();
}
