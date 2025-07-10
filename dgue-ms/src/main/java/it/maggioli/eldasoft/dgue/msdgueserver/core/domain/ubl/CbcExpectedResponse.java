package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.math.BigDecimal;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.BidType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CriterionElementType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Currency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ResponseDataType;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
public interface CbcExpectedResponse {

    CriterionElementType getCriterionElement();

    ResponseDataType getResponseDataType();

    String getUnitCode();

    Currency getCurrency();

    BidType getExpectedCode();

    BigDecimal getExpectedValueNumeric();

    BigDecimal getMaximumValueNumeric();

    BigDecimal getMinimumValueNumeric();
    
    BigDecimal getAmount();

    String getCertificationLevelDesc();
}
