package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.math.BigDecimal;
import java.util.Date;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ContributionCriterion extends ExclusionCriterion {

    private String country;
    private BigDecimal amount;
    private String currency;
    private String currency2;
    private Boolean breachEstablishedOtherThanJudicialDecision = Boolean.FALSE;
    private String meansDescription;
    private Boolean decisionFinalAndBinding = Boolean.FALSE;
    private Date dateOfConviction;
    private String periodLength;
    private Boolean eoFulfilledObligations = Boolean.FALSE;
    private String obligationsDescription;
    private BigDecimal threshold;
    private String additionalInfo;
    private Date startDate;
    private Date endDate;

    public static ContributionCriterion buildWithExists(Boolean exists) {
        ContributionCriterion taxes = new ContributionCriterion();
        taxes.setExists(exists);
        return taxes;
    }
}
