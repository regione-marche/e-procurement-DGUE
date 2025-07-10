package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import org.springframework.stereotype.Component;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SatisfiesAllCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriteriaTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriterionTypeTemplate;

@Component
class UblResponseCriteriaTransformer extends UblCriteriaTemplate {

    private final UblResponseCriterionTransformer criterionTransformer;

    UblResponseCriteriaTransformer() {
        this.criterionTransformer = new UblResponseCriterionTransformer();
    }

    @Override
    protected UblCriterionTypeTemplate getCriterionTransformer() {
        return this.criterionTransformer;
    }

	@Override
	protected Boolean satisfiesAllCriterionPresent(SatisfiesAllCriterion satisfiesAllCriterion) {
		// the 'Satisfies all' criterion is present on an ESPD Response only if its answer is 'true'
		return satisfiesAllCriterion != null && Boolean.TRUE.equals(satisfiesAllCriterion.getAnswer());
	}

}
