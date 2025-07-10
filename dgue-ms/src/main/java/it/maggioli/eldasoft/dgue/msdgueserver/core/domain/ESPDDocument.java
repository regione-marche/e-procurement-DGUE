package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.SelectionCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.PropertyUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
@Data
@Slf4j
@Valid
public class ESPDDocument implements Serializable {

    private static final long serialVersionUID = -1284904825568892458L;

    //@NotNull
    private PartyImpl authority;
    private EconomicOperatorImpl economicOperator;
    private String fileRefByCA;
    @NotNull
    @NotEmpty
    private String procedureCode;
    private List<String> weightScoringMethodologyNotes;
    private String weightingScoringType;
    private String owner;

    private ESPDRequestMetadata requestMetadata;
        
    private List<AdditionalDocumentReference> additionalDocumentReference;
        
    private String projectType;
    private String procedureTitle;
    private String procedureShortDesc;
    private List<String> cpvs;
    private String codiceANAC;

    private ProcurementProjectImpl procurementProject;

    @NotEmpty
    @NotNull
    private List<ProcurementProjectLot> lots;

    // Exclusions

    private CriminalConvictionsCriterion criminalConvictions;
    private CriminalConvictionsCriterion corruption;
    private CriminalConvictionsCriterion fraud;
    private CriminalConvictionsCriterion terroristOffences;
    private CriminalConvictionsCriterion moneyLaundering;
    private CriminalConvictionsCriterion childLabour;

    private ContributionCriterion paymentTaxes;
    private ContributionCriterion paymentSocialSecurity;

    private LawCriterion breachingObligationsEnvironmental;
    private LawCriterion breachingObligationsSocial;
    private LawCriterion breachingObligationsLabour;

    private BusinessCriterion bankruptcy;
    private BusinessCriterion insolvency;
    private BusinessCriterion arrangementWithCreditors;

    private MisconductCriterion guiltyGrave;
    private MisconductCriterion agreementsWithOtherEO;

    private ConflictInterestCriterion conflictInterest;
    private ConflictInterestCriterion involvementPreparationProcurement;

    private EarlyTerminationCriterion earlyTermination;

    private MisinterpretationCriterion guiltyMisinterpretation;

    private PurelyNationalCriterion nationalExclusionGrounds;

    // Selections

    private SuitabilityCriterion enrolmentProfessionalRegister;
    private SuitabilityCriterion enrolmentTradeRegister;
    private SuitabilityCriterion serviceContractsAuthorisation;
    private SuitabilityCriterion serviceContractsMembership;

    private TurnoverCriterion generalYearlyTurnover;
    private TurnoverCriterion averageYearlyTurnover;
    private TurnoverCriterion specificYearlyTurnover;
    private TurnoverCriterion specificAverageTurnover;

    private SetupEconomicOperatorCriterion setupEconomicOperator;
    //private FinancialRatiosCriterion financialRatio;
    private ProfessionalRiskInsuranceCriterion professionalRiskInsurance;
    private OtherEconomicFinancialCriterion otherEconomicFinancialRequirements;

    private TechnicalProfessionalCriterion workContractsPerformanceOfWorks;
    private TechnicalProfessionalCriterion supplyContractsPerformanceDeliveries;
    private TechnicalProfessionalCriterion serviceContractsPerformanceServices;
    private TechnicalProfessionalCriterion techniciansTechnicalBodies;
    private TechnicalProfessionalCriterion workContractsTechnicians;
    private TechnicalProfessionalCriterion technicalFacilitiesMeasures;
    private TechnicalProfessionalCriterion studyResearchFacilities;
    private TechnicalProfessionalCriterion supplyChainManagement;
    private TechnicalProfessionalCriterion environmentalManagementFeatures;
    private TechnicalProfessionalCriterion toolsPlantTechnicalEquipment;
    private TechnicalProfessionalCriterion educationalProfessionalQualifications;
    private TechnicalProfessionalCriterion allowanceOfChecks;
    private TechnicalProfessionalCriterion numberManagerialStaff;
    private TechnicalProfessionalCriterion averageAnnualManpower;
    //private TechnicalProfessionalCriterion subcontractingProportion;
    private TechnicalProfessionalCriterion supplyContractsSamplesDescriptionsWithoutCa;
    private TechnicalProfessionalCriterion supplyContractsSamplesDescriptionsWithCa;

    private QualityAssuranceCriterion supplyContractsCertificatesQc;
    private QualityAssuranceCriterion certificateIndependentBodiesAboutQa;
    private QualityAssuranceCriterion certificateIndependentBodiesAboutEnvironmental;

    // Others
    private OtherCriterion eoShelteredWorkshop;
    private OtherCriterion eoRegistered;
    private OtherCriterion eoTogetherWithOthers;
    private OtherCriterion reliedEntities;
    private OtherCriterion notReliedEntities;
    private OtherCriterion lotsEoTendersTo;
    private OtherCriterion eoReductionOfCandidates;
    private OtherCriterion caLots;
    private OtherCriterion eoSme;
//    private OtherCriterion suppEvidence;
//    private OtherCriterion suppEvidenceOther;
    
    ///////// request info //////////
    private String id;
    private String UUID;   
    private String issueDate;
    private String IssueTime;
    /////////////////////////////////
    
    private String location;
    private String signature;   
    private String signatureDate;
    
    

    private SatisfiesAllCriterion selectionSatisfiesAll;

    /**
     * Read the value associated with a {@link CacCriterion} stored on this domain object instance.
     *
     * @param cacCriterion The UBL criterion for which we want to retrieve the information
     *
     * @return The value stored on the ESPD domain object for the given criterion
     */
    public final ESPDCriterion readCriterionFromEspd(CacCriterion cacCriterion) {

        try {
            return (ESPDCriterion) PropertyUtils.getProperty(this, cacCriterion.getEspdDocumentField());
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            // it should never happen, the tests should cover the improper management of these values
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public final boolean getAtLeastOneSelectionCriterionWasSelected() {
        for (SelectionCriterion cacCriterion : SelectionCriterion.ALL_VALUES) {
            final ESPDCriterion espdCriterion = readCriterionFromEspd(cacCriterion);
            if (espdCriterion != null && espdCriterion.getExists()) {
                return true;
            }
        }
        return false;
    }

}
