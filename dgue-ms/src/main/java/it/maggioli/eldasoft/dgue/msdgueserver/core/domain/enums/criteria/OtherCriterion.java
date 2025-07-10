package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria;

import static java.util.Collections.unmodifiableSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.EvaluationMethodType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.infrastructure.CriterionDefinitions;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacLegislation;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcCriterionTypeCode;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OtherCriterion implements CacCriterion {

    // EO-SHELTERED
    PROCUREMENT_RESERVED("2043338f-a38a-490b-b3ec-2607cb25a017"),
    // EO-PQS
    EO_REGISTERED("9b19e869-6c89-4cc4-bd6c-ac9ca8602165"),
    // EO-GROUPS
    EO_PARTICIPATING_PROCUREMENT_PROCEDURE("ee51100f-8e3e-40c9-8f8b-57d5a15be1f2"),
    // EO-RELIED_ON-ENTITIES
    EO_RELIES_CAPACITIES("0d62c6ed-f074-4fcf-8e9f-f691351d52ad"),
    // EO-NOT_RELIED_ON-ENTITIES
    SUBCONTRACTING_THIRD_PARTIES("72c0c4b1-ca50-4667-9487-461f3eed4ed7"),
    // EO-Lots
    LOTS_EO_TENDERS_TO("8b9700b7-b13c-41e6-a220-6bbf8d5fab31"),
    // EO-REDUCTION-OF-CANDIDATES
    EO_REDUCTION_OF_CANDIDATES("51c39ba9-0444-4967-afe9-36f753b30175"),
    // CA-Lots
    CA_LOTS("6a21c421-5c1e-46f4-9762-116fbcd33097"),
    // EO SME
	EO_SME("ede30cb7-70c2-4ead-ba11-22d0cac5ab7a");
	
//	SUPP_EVIDENCE("0114620b-93e4-486e-84de-14849db1f023"),
//	
//	SUPP_EVIDENCE_OTHER("0114620b-93e4-486e-84de-14849db1f023");

    private final String uuid;

    private final String shortName;

    private final String description;

    private final CbcCriterionTypeCode criterionType;

    private final CacLegislation legislationReference;

    private final List<? extends CacRequirementGroup> groups;

    private final String espdDocumentField;

    public static final Set<OtherCriterion> ALL_VALUES = unmodifiableSet(EnumSet.allOf(OtherCriterion.class));

    OtherCriterion(String uuid) {
        final Optional<CacCriterion> criterionOptional = CriterionDefinitions.findCriterionById(uuid);
        // TODO temporary patch. Remove after JSON definition
        if (criterionOptional.isPresent()) {
            CacCriterion criterion = criterionOptional.get();
            this.uuid = criterion.getUuid();
            this.shortName = criterion.getName();
            this.description = criterion.getDescription();
            this.criterionType = criterion.getCriterionType();
            this.legislationReference = criterion.getLegislation();
            this.groups = criterion.getGroups();
            this.espdDocumentField = criterion.getEspdDocumentField();
        } else {
            this.uuid = uuid;
            this.shortName = null;
            this.description = null;
            this.criterionType = null;
            this.legislationReference = null;
            this.groups = new ArrayList<>();
            this.espdDocumentField = null;
        }
    }

    // @Override
    public String getTypeCode() {
        return this.getCriterionType().getEspdType();
    }

    @Override
    public String getName() {
        return getShortName();
    }

    @Override
    public BigDecimal getWeight() {
        return null;
    }

    @Override
    public EvaluationMethodType getEvaluationMethod() {
        return null;
    }

    @Override
    public String getEvaluationMethodDescription() {
        return null;
    }

    @Override
    public List<? extends CacCriterion> getSubTenderingCriterion() {
        return null;
    }

    @Override
    public CacLegislation getLegislation() {
        return getLegislationReference();
    }

    @Override
    public CbcCriterionTypeCode getCriterionType() {
        return this.criterionType;
    }


}
