package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SatisfiesAllCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriteriaTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriterionTypeTemplate;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Create the UBL {@link TenderingCriterionType} list of criteria for a ESPD Request, including exclusion, selection and award
 * criteria.
 * </p>
 * <p><b>The presence of a criterion in a {@link QualificationApplicationRequestType} is handled by the 'exists' flag on the {@link ESPDDocument} model {@link ESPDCriterion}.</b></p>
 * <p/>
 * The criteria need to be present in the {@link QualificationApplicationRequestType} in the following way:
 * <ol>
 * <li>All exclusion criteria except 'Purely national grounds' must be present, unless it was selected as well.</li>
 * <li>CA selects "All section criteria" -> The request contains only "All selection criteria" and not the individual ones.</li>
 * <li>CA select individual selection criteria -> The request contains only the selected ones (and even not the "All selection criteria").</li>
 * <li>CA selects no selection criteria at all -> The request contains all the selection criteria (including "All selection criteria").</li>
 * <li>The request contains only one award criterion: "Meets the objective".</li>
 * </o>
 * <p></p>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
@Component
public class UblRequestCriteriaTransformer extends UblCriteriaTemplate {

    private final UblRequestCriterionTransformer criterionTransformer;

    UblRequestCriteriaTransformer() {
        this.criterionTransformer = new UblRequestCriterionTransformer();
    }

    @Override
    protected UblCriterionTypeTemplate getCriterionTransformer() {
        return this.criterionTransformer;
    }

	@Override
	protected Boolean satisfiesAllCriterionPresent(SatisfiesAllCriterion satisfiesAllCriterion) {
		// if the CA has selected the 'Satisfies all' (which means that the 'exists' property is true)
		// then this implies that the criterion is present on the ESPD Request.
		return isCriterionSelectedByTheCA(satisfiesAllCriterion);
	}

}
