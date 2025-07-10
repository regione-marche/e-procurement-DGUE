package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria;

import static java.util.Collections.unmodifiableSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public enum SelectionCriterion implements CacCriterion {

    ALL_SELECTION_CRITERIA_SATISFIED("7e7db838-eeac-46d9-ab39-42927486f22d"),
    // Suitability-I
    ENROLMENT_PROFESSIONAL_REGISTER("6ee55a59-6adb-4c3a-b89f-e62a7ad7be7f"),
    ENROLMENT_TRADE_REGISTER("87b3fa26-3549-4f92-b8e0-3fd8f04bf5c7"),
    // Suitability-II
    SERVICE_CONTRACTS_AUTHORISATION("9eeb6d5c-0eb8-48e8-a4c5-5087a7c095a4"),
    SERVICE_CONTRACTS_MEMBERSHIP("73f10e36-ed7a-412e-995c-aa76463e3776"),
    // General_Yearly_Turnover
    GENERAL_YEARLY_TURNOVER("499efc97-2ac1-4af2-9e84-323c2ca67747"),
    // General_Average_Turnover
    AVERAGE_YEARLY_TURNOVER("b16cb9fc-6cb7-4585-9302-9533b415cf48"),
    // Specific_Average_Turnover
    SPECIFIC_AVERAGE_TURNOVER("d3dfb714-f558-4512-bbc5-e456fa2339de"),
    // Specific_Yearly_Turnover
    SPECIFIC_YEARLY_TURNOVER("074f6031-55f9-4e99-b9a4-c4363e8bc315"),
    // Setup_of_economic_operator
    SETUP_ECONOMIC_OPERATOR("77f481ce-ffb6-483f-8e2b-c78db5e68292"),
    // Financial_rations
    //FINANCIAL_RATIO("e4d37adc-08cd-4f4d-a8d8-32b62b0a1f46"),
    // Professional_risk_insurance
    PROFESSIONAL_RISK_INSURANCE("7604bd40-4462-4086-8763-a50da51a869c"),
    // Other_economic_or_financial
    OTHER_ECONOMIC_OR_FINANCIAL_REQUIREMENTS("ab0e7f2e-6418-40e2-8870-6713123e41ad"),
    // References
    WORK_CONTRACTS_PERFORMANCE_OF_WORKS("cdd3bb3e-34a5-43d5-b668-2aab86a73822"),
    SUPPLY_CONTRACTS_PERFORMANCE_OF_DELIVERIES("3a18a175-1863-4b1d-baef-588ce61960ca"),
    SERVICE_CONTRACTS_PERFORMANCE_OF_SERVICES("5e506c16-26ab-4e32-bb78-b27f87dc0565"),
    // Abilities_1
    TECHNICIANS_OR_TECHNICAL_BODIES("3aaca389-4a7b-406b-a4b9-080845d127e7"),
    WORK_CONTRACTS_TECHNICIANS_OR_TECHNICAL_BODIES("c599c130-b29f-461e-a187-4e16c7d40db7"),
    // Abilities_2
    TECHNICAL_FACILITIES_AND_MEASURES("4bf996d9-439c-40c6-9ab9-980a48cb55a1"),
    STUDY_AND_RESEARCH_FACILITIES("90a2e100-44cc-45d3-9970-69d6714f1596"),
    SUPPLY_CHAIN_MANAGEMENT("dc12a151-7fdf-4733-a8f0-30f667292e66"),
    ENVIRONMENTAL_MANAGEMENT_FEATURES("9460457e-b43d-48a9-acd1-615de6ddd33e"),
    TOOLS_PLANT_TECHNICAL_EQUIPMENT("cc18c023-211d-484d-a32e-52f3f970285f"),
    // Abilities_3
    EDUCATIONAL_AND_PROFESSIONAL_QUALIFICATIONS("07301031-2270-41af-8e7e-66fe0c777107"),
    // Abilities_4
    ALLOWANCE_OF_CHECKS("c8809aa1-29b6-4f27-ae2f-27e612e394db"),
    // Abilties_5
    NUMBER_OF_MANAGERIAL_STAFF("6346959b-e097-4ea1-89cd-d1b4c131ea4d"),
    AVERAGE_ANNUAL_MANPOWER("1f49b3f0-d50f-43f6-8b30-4bafab108b9b"),
    // Subcontracting_proportion
    //SUBCONTRACTING_PROPORTION("612a1625-118d-4ea4-a6db-413184e7c0a8"),
    // Samples_certificates
    SUPPLY_CONTRACTS_SAMPLES_DESCRIPTIONS_WITHOUT_CA("bdf0601d-2480-4250-b870-658d0ee95be6"),
    SUPPLY_CONTRACTS_SAMPLES_DESCRIPTIONS_WITH_CA("7662b7a9-bcb8-4763-a0a7-7505d8e8470d"),
    // Quality_assurance
    SUPPLY_CONTRACTS_CERTIFICATES_QC("a7669d7d-9297-43e1-9d10-691a1660187c"),
    CERTIFICATE_INDEPENDENT_BODIES_ABOUT_QA("d726bac9-e153-4e75-bfca-c5385587766d"),
    CERTIFICATE_INDEPENDENT_BODIES_ABOUT_ENVIRONMENTAL("8ed65e48-fd0d-444f-97bd-4f58da632999");

    private final String uuid;

    private final String shortName;

    private final String description;

    private final CbcCriterionTypeCode criterionType;

    private final CacLegislation legislationReference;

    private final List<? extends CacRequirementGroup> groups;

    private final String espdDocumentField;

    public static Set<SelectionCriterion> ALL_VALUES = unmodifiableSet(EnumSet.allOf(SelectionCriterion.class));

    SelectionCriterion(String uuid) {
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
    @JsonIgnore
    public String getTypeCode() {
        return CriterionType.SELECTION.name() + "." + this.getCriterionType().getEspdType();
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
    @JsonIgnore
    public CacLegislation getLegislation() {
        return legislationReference;
    }

    @Override
    public CbcCriterionTypeCode getCriterionType() {
        return this.criterionType;
    }


}
