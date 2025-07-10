package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.response;

import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriterionTypeTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblRequirementTypeTemplate;

public class UblResponseCriterionTransformer extends UblCriterionTypeTemplate {

    @Override
    protected UblRequirementTypeTemplate buildRequirementTransformer() {
        return new UblResponseRequirementTransformer();
    }

}
