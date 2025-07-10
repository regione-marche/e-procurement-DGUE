package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.request.exporting;

import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblCriterionTypeTemplate;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting.UblRequirementTypeTemplate;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;

/**
 * Transforms the criterion information coming from ESPD into a {@link TenderingCriterionType} object.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
public class UblRequestCriterionTransformer extends UblCriterionTypeTemplate {

    @Override
    protected UblRequirementTypeTemplate buildRequirementTransformer() {
        return new UblRequestRequirementTransformer();
    }
}
