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
public enum ExclusionCriterion implements CacCriterion {
    // Convictions
    PARTICIPATION_CRIMINAL_ORGANISATION("005eb9ed-1347-4ca3-bb29-9bc0db64e1ab"),
    CORRUPTION("c27b7c4e-c837-4529-b867-ed55ce639db5"),
    FRAUD("297d2323-3ede-424e-94bc-a91561e6f320"),
    TERRORIST_OFFENCES("d486fb70-86b3-4e75-97f2-0d71b5697c7d"),
    MONEY_LAUNDERING("47112079-6fec-47a3-988f-e561668c3aef"),
    CHILD_LABOUR("d789d01a-fe03-4ccd-9898-73f9cfa080d1"),
    // Contributions
    PAYMENT_OF_TAXES("b61bbeb7-690e-4a40-bc68-d6d4ecfaa3d4"),
    PAYMENT_OF_SOCIAL_SECURITY("7d85e333-bbab-49c0-be8d-c36d71a72f5e"),
    // Environ-Social-Labour_law
    BREACHING_OF_OBLIGATIONS_ENVIRONMENTAL("a80ddb62-d25b-4e4e-ae22-3968460dc0a9"),
    BREACHING_OF_OBLIGATIONS_SOCIAL("a261a395-ed17-4939-9c75-b9ff1109ca6e"),
    BREACHING_OF_OBLIGATIONS_LABOUR("a34b70d6-c43d-4726-9a88-8e2b438424bf"),
    // Business
    BANKRUPTCY("d3732c09-7d62-4edc-a172-241da6636e7c"),
    INSOLVENCY("396f288a-e267-4c20-851a-ed4f7498f137"),
    ARRANGEMENT_WITH_CREDITORS("68918c7a-f5bc-4a1a-a62f-ad8983600d48"),   
    // Misconduct
    GUILTY_OF_PROFESSIONAL_MISCONDUCT("514d3fde-1e3e-4dcd-b02a-9f984d5bbda3"),
    AGREEMENTS_WITH_OTHER_EO("56d13e3d-76e8-4f23-8af6-13e60a2ee356"),
    // Conflict_of_interest
    CONFLICT_OF_INTEREST("b1b5ac18-f393-4280-9659-1367943c1a2e"),
    DIRECT_INVOLVEMENT_PROCUREMENT_PROCEDURE("61874050-5130-4f1c-a174-720939c7b483"),
    // Early_termination
    EARLY_TERMINATION("3293e92b-7f3e-42f1-bee6-a7641bb04251"),
    // Misinterpretation
    GUILTY_OF_MISINTERPRETATION("696a75b2-6107-428f-8b74-82affb67e184"),
    // Purely_national
    NATIONAL_EXCLUSION_GROUNDS("63adb07d-db1b-4ef0-a14e-a99785cf8cf6");

    private final String uuid;

    private final String shortName;

    private final String description;

    private final CbcCriterionTypeCode criterionType;

    private final CacLegislation legislationReference;

    private final List<? extends CacRequirementGroup> groups;

    private final String espdDocumentField;
    
    private final List<? extends CacCriterion> subCriteria;

    public static final Set<ExclusionCriterion> ALL_VALUES = unmodifiableSet(EnumSet.allOf(ExclusionCriterion.class));

    ExclusionCriterion(String uuid) {
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
            this.subCriteria = criterion.getSubTenderingCriterion();
        } else {
            this.uuid = uuid;
            this.shortName = null;
            this.description = null;
            this.criterionType = null;
            this.legislationReference = null;
            this.groups = new ArrayList<>();
            this.espdDocumentField = null;
            this.subCriteria = null;
        }
    }

    // @Override
    public String getTypeCode() {
        return CriterionType.EXCLUSION.name() + "." + this.getCriterionType().getEspdType();
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
        return this.subCriteria;
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
