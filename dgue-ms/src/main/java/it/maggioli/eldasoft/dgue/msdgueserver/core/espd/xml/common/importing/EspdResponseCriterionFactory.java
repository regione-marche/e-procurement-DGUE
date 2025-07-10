package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.JsonNode;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AvailableElectronically;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.BusinessCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ConflictInterestCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ContributionCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.CriminalConvictionsCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.EarlyTerminationCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.FinancialRatiosCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.LawCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.MisconductCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.MisinterpretationCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.OtherCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.OtherEconomicFinancialCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ProfessionalRiskInsuranceCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.PurelyNationalCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.QualityAssuranceCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SatisfiesAllCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SetupEconomicOperatorCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.SuitabilityCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.TechnicalProfessionalCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.TurnoverCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.ExclusionCriterionTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.OtherCriterionTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.SelectionCriterionTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.EvaluationMethodType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.infrastructure.CriterionDefinitions;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacLegislation;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcCriterionTypeCode;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseValueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyGroupType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ExpectedAmountType;

@Slf4j
class EspdResponseCriterionFactory {

	private String workContractsPerformanceOfWorks = "cdd3bb3e-34a5-43d5-b668-2aab86a73822";
	private String supplyContractsPerformanceDeliveries = "3a18a175-1863-4b1d-baef-588ce61960ca";
	private String serviceContractsPerformanceServices = "5e506c16-26ab-4e32-bb78-b27f87dc0565";
	private String otherEconomicFinancialRequirements = "ab0e7f2e-6418-40e2-8870-6713123e41ad";
	private String workContractsTechnicians = "c599c130-b29f-461e-a187-4e16c7d40db7";
	private String purelyNationalExclusionGrounds = "63adb07d-db1b-4ef0-a14e-a99785cf8cf6";
	private String techniciansTechnicalBodies = "3aaca389-4a7b-406b-a4b9-080845d127e7";
    private final Map<String, CriterionBuilder> criterionBuilders = new HashMap<>();
	private HashMap<String,Object> values;

    EspdResponseCriterionFactory() {
        registerCriterionBuilders();
    }

    private void registerCriterionBuilders() {
    	
    	criterionBuilders.put(ExclusionCriterionTypeCode.CRIMINAL_CONVICTIONS.getEspdType(), new CriminalConvictionsCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.PAYMENT_OF_TAXES.getEspdType(), new TaxesCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.PAYMENT_OF_SOCIAL_SECURITY.getEspdType(), new TaxesCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.ENVIRONMENTAL_LAW.getEspdType(), new LawCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.SOCIAL_LAW.getEspdType(), new LawCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.LABOUR_LAW.getEspdType(), new LawCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.BANKRUPTCY_INSOLVENCY.getEspdType(), new BankruptcyCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.MISCONDUCT.getEspdType(), new MisconductDistortionCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.DISTORTING_MARKET.getEspdType(), new MisconductDistortionCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.CONFLICT_OF_INTEREST.getEspdType(), new ConflictInterestCriterionBuilder());        
        criterionBuilders.put(ExclusionCriterionTypeCode.EARLY_TERMINATION.getEspdType(), new EarlyTerminationCriterionBuilder());
        criterionBuilders.put(ExclusionCriterionTypeCode.MISINTERPRETATION.getEspdType(), new MisinterpretationCriterionBuilder());               
        criterionBuilders.put(ExclusionCriterionTypeCode.OTHER.getEspdType(), new PurelyNationalCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.ALL_CRITERIA_SATISFIED.getEspdType(), new SatisfiesAllCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.SUITABILITY.getEspdType(), new SuitabilityCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.ECONOMIC_FINANCIAL_STANDING.getEspdType(), new EconomicFinancialStandingCriterionBuilder());        
        criterionBuilders.put(SelectionCriterionTypeCode.SET_UP_EO.getEspdType(), new SetupEconomicOperatorCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.FINANCIAL_RATIO.getEspdType(), new FinancialRatiosCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.PROFESSIONAL_RISK_INSURANCE.getEspdType(), new ProfessionalRiskInsuranceCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.OTHER_EO.getEspdType(), new OtherEconomicFinancialCriterionBuilder());        
        criterionBuilders.put(SelectionCriterionTypeCode.TECHNICAL_PROFESSIONAL_ABILITY.getEspdType(), new TechnicalProfessionalCriterionBuilder());
        criterionBuilders.put(SelectionCriterionTypeCode.QUALITY_ASSURANCE.getEspdType(), new QualityAssuranceCriterionBuilder());
        criterionBuilders.put(OtherCriterionTypeCode.DATA_ON_ECONOMIC_OPERATOR.getEspdType(), new OtherCriterionBuilder());
        criterionBuilders.put(OtherCriterionTypeCode.REDUCTION_OF_CANDIDATES.getEspdType(), new OtherCriterionBuilder());
        criterionBuilders.put(OtherCriterionTypeCode.LOTS_TENDERED.getEspdType(), new OtherCriterionBuilder());
        criterionBuilders.put(OtherCriterionTypeCode.LOTS_SUBMISSION.getEspdType(), new OtherCriterionBuilder());
    }

    /**
     * Create a ESPD {@link ESPDCriterion} instance containing the appropriate information provided as UBL criteria.
     *
     * @return A freshly built {@link ESPDCriterion} containing the data coming from the XML
     *
     * @throws IllegalArgumentException If the criterion type is not recognized
     */
    ESPDCriterion buildEspdCriterion(CacCriterion cacCriterion, TenderingCriterionType ublCriterion) {
        CriterionBuilder criterionBuilder = criterionBuilders.get(cacCriterion.getCriterionType().getEspdType());
        checkArgument(criterionBuilder != null,
                "Could not build criterion '%s' with id '%s' having type code '%s'.",
                cacCriterion.getName(), cacCriterion.getUuid(), cacCriterion.getCriterionType());

        if (ublCriterion == null) {
            return criterionBuilder.buildWithExists(false);
        }

        ESPDCriterion criterion = criterionBuilder.buildWithExists(true);
        List<CacRequirementGroup> rg=new ArrayList<CacRequirementGroup>();
        //for (TenderingCriterionPropertyGroupType tcp : ublCriterion.getTenderingCriterionPropertyGroup()) {
        	
        	//rg.add(CriterionDefinitions.findRequirementGroupById(tcp.getID().getValue()));
        	//setCriterionValues(cacCriterion.getGroups(), criterion,ublCriterion.getTenderingCriterionPropertyGroup());
		//}
        if(cacCriterion.getUuid().equals(otherEconomicFinancialRequirements)){
        	        	
        	addGroupsFromNested(cacCriterion,criterion,ublCriterion.getTenderingCriterionPropertyGroup(),false,false);
        }else {
        	addGroups(cacCriterion, criterion, ublCriterion.getTenderingCriterionPropertyGroup(),false);
        }
        
        //addGroups(cacCriterion, criterion, ublCriterion.getTenderingCriterionPropertyGroup(),false);
        
        return criterion;
    }
    
    ESPDCriterion buildEspdCriterionFromResponse(CacCriterion cacCriterion, TenderingCriterionType ublCriterion,List<TenderingCriterionResponseType> questions,List<EvidenceType> evidences) {
        CriterionBuilder criterionBuilder = criterionBuilders.get(cacCriterion.getCriterionType().getEspdType());
        checkArgument(criterionBuilder != null,
                "Could not build criterion '%s' with id '%s' having type code '%s'.",
                cacCriterion.getName(), cacCriterion.getUuid(), cacCriterion.getCriterionType());

        if (ublCriterion == null) {
            return criterionBuilder.buildWithExists(false);
        }

        ESPDCriterion criterion = criterionBuilder.buildWithExists(true);
        List<CacRequirementGroup> rg=new ArrayList<CacRequirementGroup>();
               
        if(cacCriterion.getUuid().equals(workContractsPerformanceOfWorks) || 
        		cacCriterion.getUuid().equals(supplyContractsPerformanceDeliveries) || 
        		cacCriterion.getUuid().equals(serviceContractsPerformanceServices)||
        		cacCriterion.getUuid().equals(otherEconomicFinancialRequirements)){
        	
        	addGroupsFromReponseNested(cacCriterion, criterion, ublCriterion.getTenderingCriterionPropertyGroup(),false,questions,evidences,false,cacCriterion.getGroups());
        }else {
        	addGroupsFromReponse(cacCriterion, criterion, ublCriterion.getTenderingCriterionPropertyGroup(),false,questions,evidences,cacCriterion.getGroups());
        }
        
        
        return criterion;
    }
   

    private interface CriterionBuilder<T extends ESPDCriterion> {

        T buildWithExists(boolean exists);

    }

    private static class CriminalConvictionsCriterionBuilder implements CriterionBuilder<CriminalConvictionsCriterion> {

        @Override
        public CriminalConvictionsCriterion buildWithExists(boolean exists) {
            return CriminalConvictionsCriterion.buildWithExists(exists);
        }
    }

    private static class TaxesCriterionBuilder implements CriterionBuilder<ContributionCriterion> {

        @Override
        public ContributionCriterion buildWithExists(boolean exists) {
            return ContributionCriterion.buildWithExists(exists);
        }
    }

    private static class LawCriterionBuilder implements CriterionBuilder<LawCriterion> {

        @Override
        public LawCriterion buildWithExists(boolean exists) {
            return LawCriterion.buildWithExists(exists);
        }
    }

    private static class BankruptcyCriterionBuilder implements CriterionBuilder<BusinessCriterion> {

        @Override
        public BusinessCriterion buildWithExists(boolean exists) {
            return BusinessCriterion.buildWithExists(exists);
        }
    }

    private static class MisconductDistortionCriterionBuilder
            implements CriterionBuilder<MisconductCriterion> {

        @Override
        public MisconductCriterion buildWithExists(boolean exists) {
            return MisconductCriterion.buildWithExists(exists);
        }
    }

    private static class ConflictInterestCriterionBuilder implements CriterionBuilder<ConflictInterestCriterion> {

        @Override
        public ConflictInterestCriterion buildWithExists(boolean exists) {
            return ConflictInterestCriterion.buildWithExists(exists);
        }
    }

    private static class PurelyNationalCriterionBuilder implements CriterionBuilder<PurelyNationalCriterion> {

        @Override
        public PurelyNationalCriterion buildWithExists(boolean exists) {
            return PurelyNationalCriterion.buildWithExists(exists);
        }
    }
    
    private static class EarlyTerminationCriterionBuilder implements CriterionBuilder<EarlyTerminationCriterion> {

        @Override
        public EarlyTerminationCriterion buildWithExists(boolean exists) {
            return EarlyTerminationCriterion.buildWithExists(exists);
        }
    }

    private static class MisinterpretationCriterionBuilder implements CriterionBuilder<MisinterpretationCriterion> {

        @Override
        public MisinterpretationCriterion buildWithExists(boolean exists) {
            return MisinterpretationCriterion.buildWithExists(exists);
        }
    }

    private static class SatisfiesAllCriterionBuilder implements CriterionBuilder<SatisfiesAllCriterion> {

        @Override
        public SatisfiesAllCriterion buildWithExists(boolean exists) {
            return SatisfiesAllCriterion.buildWithExists(exists);
        }
    }

    private static class SuitabilityCriterionBuilder implements CriterionBuilder<SuitabilityCriterion> {

        @Override
        public SuitabilityCriterion buildWithExists(boolean exists) {
            return SuitabilityCriterion.buildWithExists(exists);
        }
    }

    private static class EconomicFinancialStandingCriterionBuilder
            implements CriterionBuilder<TurnoverCriterion> {

        @Override
        public TurnoverCriterion buildWithExists(boolean exists) {
            return TurnoverCriterion.buildWithExists(exists);
        }
    }
    

    private static class SetupEconomicOperatorCriterionBuilder implements CriterionBuilder<SetupEconomicOperatorCriterion> {

		@Override
		public SetupEconomicOperatorCriterion buildWithExists(boolean exists) {
		    return SetupEconomicOperatorCriterion.buildWithExists(exists);
		}
    }
    
    private static class FinancialRatiosCriterionBuilder implements CriterionBuilder<FinancialRatiosCriterion> {

		@Override
		public FinancialRatiosCriterion buildWithExists(boolean exists) {
		    return FinancialRatiosCriterion.buildWithExists(exists);
		}
    }
    private static class OtherEconomicFinancialCriterionBuilder implements CriterionBuilder<OtherEconomicFinancialCriterion> {

		@Override
		public OtherEconomicFinancialCriterion buildWithExists(boolean exists) {
		    return OtherEconomicFinancialCriterion.buildWithExists(exists);
		}
    }
    private static class ProfessionalRiskInsuranceCriterionBuilder implements CriterionBuilder<ProfessionalRiskInsuranceCriterion> {

		@Override
		public ProfessionalRiskInsuranceCriterion buildWithExists(boolean exists) {
		    return ProfessionalRiskInsuranceCriterion.buildWithExists(exists);
		}
    }

    

    private static class TechnicalProfessionalCriterionBuilder
            implements CriterionBuilder<TechnicalProfessionalCriterion> {

        @Override
        public TechnicalProfessionalCriterion buildWithExists(boolean exists) {
            return TechnicalProfessionalCriterion.buildWithExists(exists);
        }
    }

    private static class QualityAssuranceCriterionBuilder implements CriterionBuilder<QualityAssuranceCriterion> {

        @Override
        public QualityAssuranceCriterion buildWithExists(boolean exists) {
            return QualityAssuranceCriterion.buildWithExists(exists);
        }
    }
    


    private static class OtherCriterionBuilder implements CriterionBuilder<OtherCriterion> {

        @Override
        public OtherCriterion buildWithExists(boolean exists) {
            return OtherCriterion.build();
        }
    }

    
    private CacRequirementGroup findRequirementGroupById(List<? extends CacRequirementGroup> requirementGroup, String groupId) {
    	CacRequirementGroup reqGroup = null;
    	for (CacRequirementGroup cacRequirementGroup : requirementGroup) {
    		if(cacRequirementGroup.getId().equals(groupId)) { 
    			return cacRequirementGroup;    			
    		}else {    			
    			if(reqGroup == null && cacRequirementGroup.getSubgroups() != null) {
    				reqGroup = findRequirementGroupById(cacRequirementGroup.getSubgroups(),groupId);
    			}
    		}
    	}
    	return reqGroup;
    	
    }     
    
    private CacRequirementGroup findRequirementGroupByIdForProperties(List<? extends CacRequirementGroup> requirementGroup, String groupId,int groupSize) {
    	CacRequirementGroup reqGroup = null;
    	for (CacRequirementGroup cacRequirementGroup : requirementGroup) {  
    		if(cacRequirementGroup.getId().equals(groupId) && cacRequirementGroup.getRequirements().size() == groupSize) { 
    			return cacRequirementGroup;    			
    		}else {    			
    			if(reqGroup == null && cacRequirementGroup.getSubgroups() != null) {
    				reqGroup = findRequirementGroupByIdForProperties(cacRequirementGroup.getSubgroups(),groupId,groupSize);
    			}
    		}
    	}
    	return reqGroup;
    	
    }
       

    
    private void addGroupsFromNested(CacCriterion cacCriterion, ESPDCriterion espdCriterion, List<TenderingCriterionPropertyGroupType> criterionType,
    		boolean array, boolean unbuonded) {
		if (isEmpty(cacCriterion.getGroups())) {
			return;
		}
		
		
		DynamicRequirementGroup d=null;
		List<TenderingCriterionPropertyType> listArray=null;
		try {			
			for (TenderingCriterionPropertyGroupType group : criterionType) {				
				
				CacRequirementGroup reqGroup2=CriterionDefinitions.findRequirementGroupById(group.getID().getValue());										
				CacRequirementGroup reqGroup = findRequirementGroupById(cacCriterion.getGroups(),group.getID().getValue());
				if (d == null) {
					
					d=new DynamicRequirementGroup(); 
				}
				if(reqGroup.isUnbounded()) {	
					unbuonded=true;
					array=false;
					log.info("Unbounded: true");
					if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() == null) {
						PropertyUtils.setProperty(espdCriterion, "unboundedGroups", new ArrayList<DynamicRequirementGroup>(group.getSubsidiaryTenderingCriterionPropertyGroup().size()));
					}					
				
				}else if (reqGroup.isArray()){
					array=true;
					unbuonded=false;
					listArray=group.getTenderingCriterionProperty();
					
				}
					
				addProperyGroup(cacCriterion,group.getID().getValue(),group.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,array,listArray);											
				if(d != null && d.isEmpty()) {
					array=false;
					addGroupsFromNested(cacCriterion,espdCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),array,unbuonded);
				}else {
					
					addSubsidiary(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray);
				}
				
					
			}
			if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null) {
				if(d != null) {				
					((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups().add(d);
				}
			}	
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			log.error("addGroupsFromNested si è verificato un errore", e);
		}
			
	}
    
    private void addGroups(CacCriterion cacCriterion, ESPDCriterion espdCriterion, List<TenderingCriterionPropertyGroupType> criterionType,boolean array) {
		if (isEmpty(cacCriterion.getGroups())) {
			return;
		}
		boolean unbuonded=false;
		//boolean array=false;
		DynamicRequirementGroup d=null;
		List<TenderingCriterionPropertyType> listArray=null;
		try {
			//PropertyUtils.setProperty(espdCriterion, "unboundedGroups", null);
			for (TenderingCriterionPropertyGroupType group : criterionType) {		
				
				
				CacRequirementGroup reqGroup2=CriterionDefinitions.findRequirementGroupById(group.getID().getValue());
				CacRequirementGroup reqGroup=findRequirementGroupById(cacCriterion.getGroups(),group.getID().getValue());
				log.info("cerco gruppo con id:"+ group.getID().getValue());
				d=new DynamicRequirementGroup(); 
				if(reqGroup != null && reqGroup.isUnbounded()) {	
					unbuonded=true;
					array=false;
					log.info("Unbounded: true");
					if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() == null) {
						PropertyUtils.setProperty(espdCriterion, "unboundedGroups", new ArrayList<DynamicRequirementGroup>(group.getSubsidiaryTenderingCriterionPropertyGroup().size()));
					}					
					
				}else if (reqGroup != null  && reqGroup.isArray()){
					array=true;
					listArray=group.getTenderingCriterionProperty();
					
				}
					
				addProperyGroup(cacCriterion,group.getID().getValue(),group.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,array,listArray);											
				if(d != null && d.isEmpty()) {
					array=false;
					addGroups(cacCriterion,espdCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),array);
				}else {
					
					addSubsidiary(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray);
				}
				
					
				if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null) {
					if(d != null && !d.isEmpty()) {				
						((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups().add(d);
					}
				}	
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error("",e);		
		}
			
	}
    
    
    private void addSubsidiary(CacCriterion cacCriterion,List<TenderingCriterionPropertyGroupType> subsidiary, ESPDCriterion espdCriterion,DynamicRequirementGroup d,boolean unbuonded,boolean array,List<TenderingCriterionPropertyType> listArray) {
    	boolean flag=false;    	
		for (TenderingCriterionPropertyGroupType tenderingCriterionPropertyGroupType : subsidiary) {
			DynamicRequirementGroup d2 = new DynamicRequirementGroup();
			CacRequirementGroup reqGroup=CriterionDefinitions.findRequirementGroupById(tenderingCriterionPropertyGroupType.getID().getValue());
			log.info(reqGroup.getId() != null ? "Group: "+reqGroup.getId() : "");
			if(reqGroup.isUnbounded()) {
				log.info("	Unbounded: true");
				unbuonded=true;
				

				if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d.getUnboundedGroups() == null) {
					flag=true;					
					d.put("unboundedGroups", new ArrayList<DynamicRequirementGroup>());
					d.setUnboundedGroups(new ArrayList<DynamicRequirementGroup>());					
				}

				
				addProperyGroup(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d2,unbuonded,false,null);
				addSubsidiary(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d2,unbuonded,false,null);
				if(cacCriterion.getUuid().equals(otherEconomicFinancialRequirements)) {
					if(unbuonded && d.getUnboundedGroups() != null) {					
						if(d2 != null && !d2.isEmpty()) {	
							if(d2.get("minRating") != null || 
								d2.get("ratingScheme") != null) {
								
								d2.put("__nomeGruppo__", "unbound2");
							}else {
								d2.put("__nomeGruppo__", "unbound1");
							}
							DynamicRequirementGroup drg=new DynamicRequirementGroup();
							drg=d2;						
							((List<DynamicRequirementGroup>)d.get("unboundedGroups")).add(drg);
						}
					}
				} else if(flag && unbuonded && d.getUnboundedGroups() != null) {					
					if(d2 != null && !d2.isEmpty()) {	
						DynamicRequirementGroup drg=new DynamicRequirementGroup();
						drg=d2;						
						((List<DynamicRequirementGroup>)d.get("unboundedGroups")).add(drg);
					}
				} 
				
			}else if (reqGroup.isArray()){
				addProperyGroup(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty());
				addSubsidiary(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty());
				
			} else {
				
				addProperyGroup(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,false,null);
				addSubsidiary(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,false,null);
				
			}
		}		
	}
    
    

    private void addProperyGroup(CacCriterion cacCriterion,String groupId,List<TenderingCriterionPropertyType> property, ESPDCriterion espdCriterion,
    		DynamicRequirementGroup d,boolean unbuonded,boolean array,List<TenderingCriterionPropertyType> listArray) {
    	
		for (TenderingCriterionPropertyType tenderingCriterionPropertyType : property) {
			if(tenderingCriterionPropertyType != null) {

				
					
				
				CacRequirementGroup reqGroup = null;
				
								
				reqGroup = findRequirementGroupById(cacCriterion.getGroups(),groupId);													
				
				String name = null;
				if(tenderingCriterionPropertyType.getName() != null) {
					name=tenderingCriterionPropertyType.getName().getValue();
				}
				
				CacCriterionRequirement requirementById = null;
				if(reqGroup != null) {
					requirementById = findRequirement(reqGroup.getRequirements(),tenderingCriterionPropertyType.getDescription().get(0).getValue(),name,tenderingCriterionPropertyType.getValueDataTypeCode().getValue(),tenderingCriterionPropertyType.getTypeCode().getValue());
				}
				
				if(requirementById != null) {
					log.info("		Property: "+requirementById.getEspdCriterionFields().get(0));
					String espdFieldName = requirementById.getEspdCriterionFields().get(0);
					String espdFieldName2 = null; 
					if(requirementById.getEspdCriterionFields().size() > 1) {
						espdFieldName2 = requirementById.getEspdCriterionFields().get(1);
					}
					Object requirementValue = null;
					Object start = null;
					Object end = null;
					if(tenderingCriterionPropertyType != null) {     
						
						if("REQUIREMENT".equals(tenderingCriterionPropertyType.getTypeCode().getValue())) {              			                		   
							switch (requirementById.getResponseType().getCode()) {
							case "DESCRIPTION":
								if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
								}
								break;
							case "LOT_IDENTIFIER":
								if(tenderingCriterionPropertyType.getExpectedID() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedID().getValue();
								}
								break;
							case "ECONOMIC_OPERATOR_IDENTIFIER":
								if(tenderingCriterionPropertyType.getExpectedID() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedID().getValue();
								}
							break;
							case "EVIDENCE_URL":
								if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
								}
								break;
							case "URL":
								if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
								}
								break;
							case "AMOUNT":
								if(tenderingCriterionPropertyType.getExpectedAmount() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedAmount();
								}
								break;
							case "QUANTITY_INTEGER":
								if(tenderingCriterionPropertyType.getExpectedValueNumeric() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedValueNumeric().getValue();
								}
								break;
							case "CODE":
								if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedCode().getValue();
								}
								break;
							case "PERIOD":							
								if(tenderingCriterionPropertyType.getApplicablePeriod() != null && tenderingCriterionPropertyType.getApplicablePeriod().size()>0) {        			
									try {
										start = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("yyyy-MM-dd").parse(tenderingCriterionPropertyType.getApplicablePeriod().get(0).getStartDate().getValue().toString()));
										end = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("yyyy-MM-dd").parse(tenderingCriterionPropertyType.getApplicablePeriod().get(0).getEndDate().getValue().toString()));
									} catch (ParseException e) {
										log.error("Errore converisone data: ",e);
									}
									
									
									
								}
								break;
							case "QUANTITY":
								if(tenderingCriterionPropertyType.getExpectedValueNumeric() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedValueNumeric().getValue();
								}
								break;
							case "CODE_BOOLEAN":
								if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
									requirementValue = tenderingCriterionPropertyType.getExpectedCode().getValue();
								}
								break;
							case "WEIGHT_INDICATOR":
								if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
									requirementValue = Boolean.valueOf(tenderingCriterionPropertyType.getExpectedCode().getValue());
								}else {
									requirementValue = false;
								}
								break;        			
							default:
								break;
							}
							
						}
					}
					if(start != null || end != null) {
						log.info("		Property value: "+start+"----"+end);
						try {
							if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d != null) {														
								d.put(espdFieldName, start);
								d.put(espdFieldName2, end);						
							} else{
								//d=null;							
								PropertyUtils.setProperty(espdCriterion, espdFieldName,new SimpleDateFormat("yyyy-MM-dd").parse(start.toString()));
								PropertyUtils.setProperty(espdCriterion, espdFieldName2,new SimpleDateFormat("yyyy-MM-dd").parse(end.toString()));														
							}
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
							// TODO Auto-generated catch block
							log.error("addProperyGroup si è verificato un errore", e);
						}
					}else {					
						if(requirementValue != null) {
							log.info("		Property value: "+requirementValue);
							try {
								if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d != null) {							
									if (requirementValue instanceof ExpectedAmountType) {
										// values which represent amounts are special and need to be stored in two fields
										d.put(espdFieldName, ((ExpectedAmountType) requirementValue).getValue());
										d.put(requirementById.getEspdCriterionFields().get(1), ((ExpectedAmountType) requirementValue).getCurrencyID());
									} else {
										if("lotId".equals(espdFieldName)) {
											ArrayList<String> lots = new ArrayList<String>();
											if((ArrayList<String>)d.get("lotId") != null) {										
												ArrayList<String> lot = (ArrayList<String>)d.get("lotId");
												lot.add((String)requirementValue);
												d.put(espdFieldName, lot);
											} else {
												lots.add((String)requirementValue);
												d.put(espdFieldName, lots);
											}
										} else {
											d.put(espdFieldName, requirementValue);
										}
										
										
									}
									
									
								}else if(array) {
									List<HashMap<String,Object>> temp;
									if(PropertyUtils.getProperty(espdCriterion,espdFieldName) != null) {
										temp=(List<HashMap<String,Object>>) PropertyUtils.getProperty(espdCriterion,espdFieldName);
										
									}else {
										PropertyUtils.setProperty(espdCriterion, espdFieldName, new ArrayList<String>());
										temp=(List<HashMap<String,Object>>) PropertyUtils.getProperty(espdCriterion,espdFieldName);
									}
									HashMap<String,Object> arrObj = new HashMap<>();
									arrObj.put(espdFieldName, requirementValue);
									temp.add(arrObj);
									PropertyUtils.setProperty(espdCriterion, espdFieldName, temp);
									for (TenderingCriterionPropertyType tcpg : listArray) {
										//d.put(espdFieldName, tcpg.get);
									}
								}				
								else{
									d=null;
									if (requirementValue instanceof ExpectedAmountType) {
										PropertyUtils.setProperty(espdCriterion, espdFieldName, ((ExpectedAmountType) requirementValue).getValue());
										PropertyUtils.setProperty(espdCriterion, requirementById.getEspdCriterionFields().get(1),
												((ExpectedAmountType) requirementValue).getCurrencyID());
										
									} else {
										
										PropertyUtils.setProperty(espdCriterion, espdFieldName, requirementValue);
										
									}
								}
							} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
								// TODO Auto-generated catch block
								log.error("addProperyGroup si è verificato un errore", e);
							}
						}
					}
				}
			
			}
		}	
		
		
	}
    
//    private CacCriterionRequirement findRequirement(List<? extends CacCriterionRequirement>  requirements,String description,String responseType,String typeCode) {
//    	CacCriterionRequirement req=null;
//    	for (CacCriterionRequirement cacCriterionRequirement : requirements) {
//			if(cacCriterionRequirement.getResponseType().getCode().equals(responseType) &&
//					cacCriterionRequirement.getDescription().toUpperCase().equals(description.toUpperCase()) &&
//					(typeCode != null && cacCriterionRequirement.getExpectedResponse().getCriterionElement().getCode()!=null && cacCriterionRequirement.getExpectedResponse().getCriterionElement().getCode().toUpperCase().equals(typeCode.toUpperCase()))) {
//				req=cacCriterionRequirement;
//				
//			}
//		} 
//    	 
//    	return req;
//    	
//    }
//    
    private CacCriterionRequirement findRequirement(List<? extends CacCriterionRequirement>  requirements,String description,String name,String responseType,String typeCode) {
    	CacCriterionRequirement req=null;
    	if(requirements.size() == 1) {
    		return requirements.get(0);
    	}
    	for (CacCriterionRequirement cacCriterionRequirement : requirements) {
			if(cacCriterionRequirement.getResponseType().getCode().equals(responseType) &&
			   cacCriterionRequirement.getExpectedResponse().getCriterionElement().getCode().toUpperCase().equals(typeCode.toUpperCase()) &&
			   (typeCode != null && cacCriterionRequirement.getExpectedResponse().getCriterionElement().getCode()!=null) &&
					
			   ((name != null && cacCriterionRequirement.getName().toUpperCase().equals(name.toUpperCase())) ||
			   (cacCriterionRequirement.getDescriptionSearch() != null && description.contains(cacCriterionRequirement.getDescriptionSearch())) ||
			   (description != null && cacCriterionRequirement.getDescription().toUpperCase().equals(description.toUpperCase())))
					
					) {
				req=cacCriterionRequirement;
				
			}
		} 
    	 
    	return req;
    	
    }
    
    
    
    
    //FROM RESPONSE
    
    
    
    private void addGroupsFromReponse(CacCriterion cacCriterion, ESPDCriterion espdCriterion, List<TenderingCriterionPropertyGroupType> criterionType,
    		boolean array,List<TenderingCriterionResponseType> questions,List<EvidenceType> evidences, List<? extends CacRequirementGroup> gruppiRicorsivi) {
		if (isEmpty(cacCriterion.getGroups())) {
			return;
		}
		boolean enterUnboundedGroups2 = false;
		boolean unbuonded=false;		
		DynamicRequirementGroup d=null;
		List<TenderingCriterionPropertyType> listArray=null;
		try {
			int index = 0;
			
			for (TenderingCriterionPropertyGroupType group : criterionType) {		
				
				
				//CacRequirementGroup reqGroup2=CriterionDefinitions.findRequirementGroupById(group.getID().getValue());
				log.info("search group: {}",group.getID().getValue());
				CacRequirementGroup reqGroup=findRequirementGroupById(cacCriterion.getGroups(),group.getID().getValue());
				CacRequirementGroup requirementGroup = null;
				if(gruppiRicorsivi != null && gruppiRicorsivi.size() > index) {
					requirementGroup = gruppiRicorsivi.get(index);
					log.info(gruppiRicorsivi != null ? "####################################### " +gruppiRicorsivi.get(index).getName() : "");
				}
				
				if(reqGroup != null){
					log.info("Group (addGroupsFromReponse): {}",reqGroup.getId());
				}

				
				d=new DynamicRequirementGroup(); 
				if(reqGroup != null && reqGroup.isUnbounded()) {
					unbuonded=true;
					array=false;
					log.info("Unbounded: true");
					if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() == null) {
						PropertyUtils.setProperty(espdCriterion, "unboundedGroups", new ArrayList<DynamicRequirementGroup>(group.getSubsidiaryTenderingCriterionPropertyGroup().size()));
					}					
					
				}else if (reqGroup != null && reqGroup.isArray()){
					array=true;
					listArray=group.getTenderingCriterionProperty();
					unbuonded=false;
				} else {
					unbuonded=false;
				}
				if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){					
					addProperyGroupFromReponse(cacCriterion,group.getID().getValue(),group.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,requirementGroup);				
				} else {
					addProperyGroupFromReponse(cacCriterion,group.getID().getValue(),group.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,null);
				}														
				if(d != null && d.isEmpty()) {
					array=false;
					if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
						if(index >= gruppiRicorsivi.size()) {
							index--;
						}
						addGroupsFromReponse(cacCriterion,espdCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),array,questions,evidences,gruppiRicorsivi.get(index).getSubgroups());
					} else {
						addGroupsFromReponse(cacCriterion,espdCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),array,questions,evidences,null);
					}
				}else {
					if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
						if(index >= gruppiRicorsivi.size()) {
							index--;
						}
						addSubsidiaryFromReponse(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,gruppiRicorsivi.get(index).getSubgroups());	
					} else {
						addSubsidiaryFromReponse(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,null);
					}
					
				}
					
				if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null) {
					if(d != null) {		
						if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds) && (requirementGroup!= null && requirementGroup.getName().equals("G1.2.1"))){
							PropertyUtils.setProperty(espdCriterion, "unboundedGroups2", new ArrayList<DynamicRequirementGroup>());
							((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups2().add(d);
							enterUnboundedGroups2 = true;
						} else {
							if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds) && enterUnboundedGroups2) {
								((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups2().add(d);
							} else {
								((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups().add(d);
							}
							
						}
					}
				}	
				
				log.info("########index: {}", index);
				index++;				
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error("addGroupsFromReponse si è verificato un errore", e);
		}			
	}
    
    private void addGroupsFromReponseNested(CacCriterion cacCriterion, ESPDCriterion espdCriterion, List<TenderingCriterionPropertyGroupType> criterionType,
    		boolean array,List<TenderingCriterionResponseType> questions,List<EvidenceType> evidences, boolean unbuonded,List<? extends CacRequirementGroup> gruppiRicorsivi) {
		if (isEmpty(cacCriterion.getGroups())) {
			return;
		}
		
		
		DynamicRequirementGroup d=null;
		List<TenderingCriterionPropertyType> listArray=null;
		try {			
			int index = 0;
			for (TenderingCriterionPropertyGroupType group : criterionType) {
				if(group != null && group.getName() != null) {
					
				}
				log.info("grop: "+ group.getName());
				CacRequirementGroup requirementGroup = null;
				index++;
				if(gruppiRicorsivi != null && gruppiRicorsivi.size() > index) {
					requirementGroup = gruppiRicorsivi.get(index);
					log.info(gruppiRicorsivi != null ? "####################################### " +gruppiRicorsivi.get(index).getName() : "");
				}
				
				CacRequirementGroup reqGroup2=CriterionDefinitions.findRequirementGroupById(group.getID().getValue());										
				CacRequirementGroup reqGroup = findRequirementGroupById(cacCriterion.getGroups(),group.getID().getValue());
				if (d == null) {
					
					d=new DynamicRequirementGroup(); 
				}
				if(reqGroup.isUnbounded()) {	
					unbuonded=true;
					array=false;
					log.info("Unbounded: true");
					if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() == null) {
						PropertyUtils.setProperty(espdCriterion, "unboundedGroups", new ArrayList<DynamicRequirementGroup>(group.getSubsidiaryTenderingCriterionPropertyGroup().size()));
					}					
				
				}else if (reqGroup.isArray()){
					array=true;
					unbuonded=false;
					listArray=group.getTenderingCriterionProperty();
					
				}
					
				addProperyGroupFromReponse(cacCriterion,group.getID().getValue(),group.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,requirementGroup);											
				if(d != null && d.isEmpty()) {
					array=false;
					addGroupsFromReponseNested(cacCriterion,espdCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),array,questions,evidences,unbuonded,cacCriterion.getGroups());
					
				}else {
					if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
						if(index >= gruppiRicorsivi.size()) {
							index--;
						}
						addSubsidiaryFromReponse(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,gruppiRicorsivi.get(index).getSubgroups());
					} else {
						addSubsidiaryFromReponse(cacCriterion,group.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,array,listArray,questions,evidences,null);
					}
					
				}
				
				
				
					
			}
			if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null) {
				if(d != null) {				
					((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups().add(d);
				}
			}	
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			log.error("addGroupsFromReponseNested si è verificato un errore", e);
		}
			
	}
    
              
    private void addSubsidiaryFromReponse(CacCriterion cacCriterion,List<TenderingCriterionPropertyGroupType> subsidiary, ESPDCriterion espdCriterion,
    		DynamicRequirementGroup d,boolean unbuonded,boolean array,List<TenderingCriterionPropertyType> listArray,List<TenderingCriterionResponseType> questions,
    		List<EvidenceType> evidences,List<? extends CacRequirementGroup> gruppiRicorsivi) {
    	boolean flag=false;    	
    	int index = 0;
		for (TenderingCriterionPropertyGroupType tenderingCriterionPropertyGroupType : subsidiary) {
			DynamicRequirementGroup d2 = new DynamicRequirementGroup();
			CacRequirementGroup reqGroup=CriterionDefinitions.findRequirementGroupById(tenderingCriterionPropertyGroupType.getID().getValue());
			
			CacRequirementGroup requirementGroup = null;
			if(gruppiRicorsivi != null && gruppiRicorsivi.size() > index) {
				requirementGroup = gruppiRicorsivi.get(index);
				log.info(gruppiRicorsivi != null ? "####################################### " +gruppiRicorsivi.get(index).getName() : "");
			}
			
			log.info("	Group: "+reqGroup.getId());
			
			if(reqGroup.isUnbounded()) {
				log.info("	Unbounded: true");
				unbuonded=true;
				

				if(((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d.getUnboundedGroups() == null) {
					flag=true;					
					d.put("unboundedGroups", new ArrayList<DynamicRequirementGroup>());
					d.setUnboundedGroups(new ArrayList<DynamicRequirementGroup>());					
				}

				if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
					if(index >= gruppiRicorsivi.size()) {
						index--;
					}
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d2,unbuonded,false,null,questions,evidences,requirementGroup);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d2,unbuonded,false,null,questions,evidences,gruppiRicorsivi.get(index).getSubgroups());					
				} else {
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d2,unbuonded,false,null,questions,evidences,null);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d2,unbuonded,false,null,questions,evidences,null);
					
				}
				if(cacCriterion.getUuid().equals(workContractsTechnicians)) {
					if(unbuonded && d.getUnboundedGroups() != null) {					
						if(d2 != null && !d2.isEmpty()) {	
							if(d2.get("firstName") != null || 
								d2.get("lastName") != null || 
								d2.get("profession") != null || 
								d2.get("experience") != null || 
								d2.get("otherInfoTech") != null || 
								d2.get("howLong") != null) {
								
								d2.put("__nomeGruppo__", "unbound1");
							}else {
								d2.put("__nomeGruppo__", "unbound2");
							}
							DynamicRequirementGroup drg=new DynamicRequirementGroup();
							drg=d2;						
							((List<DynamicRequirementGroup>)d.get("unboundedGroups")).add(drg);
						}
					}
				} else if(cacCriterion.getUuid().equals(otherEconomicFinancialRequirements)) {
					if(unbuonded && d.getUnboundedGroups() != null) {					
						if(d2 != null && !d2.isEmpty()) {	
							if(d2.get("minRating") != null || 
								d2.get("ratingScheme") != null) {
								
								d2.put("__nomeGruppo__", "unbound2");
							}else {
								d2.put("__nomeGruppo__", "unbound1");
							}
							DynamicRequirementGroup drg=new DynamicRequirementGroup();
							drg=d2;						
							((List<DynamicRequirementGroup>)d.get("unboundedGroups")).add(drg);
						}
					}
				} else {
					if(flag && unbuonded && d.getUnboundedGroups() != null) {					
						if(d2 != null && !d2.isEmpty()) {	
							DynamicRequirementGroup drg=new DynamicRequirementGroup();
							drg=d2;						
							((List<DynamicRequirementGroup>)d.get("unboundedGroups")).add(drg);
						}
					}
				}
				
				
			}else if (reqGroup.isArray()){
				if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
					if(index >= gruppiRicorsivi.size()) {
						index--;
					}
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),questions,evidences,requirementGroup);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),questions,evidences,gruppiRicorsivi.get(index).getSubgroups());					
				} else {
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),questions,evidences,null);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,true,tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),questions,evidences,null);					
				}
				
			} else {
				if(cacCriterion.getUuid().equals(purelyNationalExclusionGrounds)){
					if(index >= gruppiRicorsivi.size()) {
						index--;
					}
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,false,null,questions,evidences,requirementGroup);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,false,null,questions,evidences,gruppiRicorsivi.get(index).getSubgroups());					
				} else {
					addProperyGroupFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getID().getValue(),tenderingCriterionPropertyGroupType.getTenderingCriterionProperty(),espdCriterion,d,unbuonded,false,null,questions,evidences,null);
					addSubsidiaryFromReponse(cacCriterion,tenderingCriterionPropertyGroupType.getSubsidiaryTenderingCriterionPropertyGroup(),espdCriterion,d,unbuonded,false,null,questions,evidences,null);					
				}
				
			}
			log.info("########index:", index);
			index++;
		}	
	}
    
    

    private void addProperyGroupFromReponse(CacCriterion cacCriterion,String groupId,List<TenderingCriterionPropertyType> property, ESPDCriterion espdCriterion,
    		DynamicRequirementGroup d,boolean unbuonded,boolean array,List<TenderingCriterionPropertyType> listArray,
    		List<TenderingCriterionResponseType> questions,List<EvidenceType> evidences, CacRequirementGroup reqGroup) {
    	
		for (TenderingCriterionPropertyType tenderingCriterionPropertyType : property) {
			
			//CacRequirementGroup reqGroup = null;
			
//			if(cacCriterion.getUuid().equals(workContractsTechnicians) || cacCriterion.getUuid().equals(techniciansTechnicalBodies) || cacCriterion.getUuid().equals(techniciansTechnicalBodies)) {				
//				reqGroup = findRequirementGroupByIdForProperties(cacCriterion.getGroups(),groupId,property.size());													
//			}else {
//				reqGroup = findRequirementGroupById(cacCriterion.getGroups(),groupId);		
//				
//			}
			
			if(reqGroup == null) {
				if(cacCriterion.getUuid().equals(workContractsTechnicians) || cacCriterion.getUuid().equals(techniciansTechnicalBodies) || cacCriterion.getUuid().equals(techniciansTechnicalBodies)) {				
					reqGroup = findRequirementGroupByIdForProperties(cacCriterion.getGroups(),groupId,property.size());													
				}else {
					reqGroup = findRequirementGroupById(cacCriterion.getGroups(),groupId);							
				}
			}
			
			String name = null;
			if(tenderingCriterionPropertyType.getName() != null) {
				name=tenderingCriterionPropertyType.getName().getValue();
			}
			if(reqGroup != null) {
				log.info("reqgroup: "+reqGroup.getName());
			}
			
			CacCriterionRequirement requirementById = findRequirement(reqGroup.getRequirements(),tenderingCriterionPropertyType.getDescription().get(0).getValue(),name,tenderingCriterionPropertyType.getValueDataTypeCode().getValue(),tenderingCriterionPropertyType.getTypeCode().getValue());
			if(requirementById != null) {
				log.info("		Property: "+requirementById.getEspdCriterionFields().get(0));
				String espdFieldName2 = null;
				String espdFieldName = requirementById.getEspdCriterionFields().get(0);
				if(requirementById.getEspdCriterionFields().size() > 1) {
					espdFieldName2 = requirementById.getEspdCriterionFields().get(1);
				}
				Object requirementValue = null;
				Object start = null;
				Object end = null;
				String evidence = null;
				if(tenderingCriterionPropertyType != null) {     
					
					if("REQUIREMENT".equals(tenderingCriterionPropertyType.getTypeCode().getValue())) {              			                		   
						switch (requirementById.getResponseType().getCode()) {
						case "DESCRIPTION":
							if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
							}
							break;
						case "LOT_IDENTIFIER":
							if(tenderingCriterionPropertyType.getExpectedID() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedID().getValue();
							}
							break;
						case "ECONOMIC_OPERATOR_IDENTIFIER":
							if(tenderingCriterionPropertyType.getExpectedID() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedID().getValue();
							}
						break;
						case "EVIDENCE_URL":
							if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
							}
							break;
						case "URL":
							if(tenderingCriterionPropertyType.getExpectedDescription() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedDescription().getValue();
							}
							break;
						case "AMOUNT":
							if(tenderingCriterionPropertyType.getExpectedAmount() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedAmount();
							}
							break;
						case "QUANTITY_INTEGER":
							if(tenderingCriterionPropertyType.getExpectedValueNumeric() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedValueNumeric().getValue();
							}
							break;
						case "CODE":
							if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedCode().getValue();
							}
							break;
						case "PERIOD":							
							if(tenderingCriterionPropertyType.getApplicablePeriod() != null && tenderingCriterionPropertyType.getApplicablePeriod().size()>0) {        			
								try {
									start = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("yyyy-MM-dd").parse(tenderingCriterionPropertyType.getApplicablePeriod().get(0).getStartDate().getValue().toString()));
									end = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("yyyy-MM-dd").parse(tenderingCriterionPropertyType.getApplicablePeriod().get(0).getEndDate().getValue().toString()));
								} catch (ParseException e) {
									log.error("Errore converisone data: ",e);
								}
							}
							break;
						case "QUANTITY":
							if(tenderingCriterionPropertyType.getExpectedValueNumeric() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedValueNumeric().getValue();
							}
							break;
						case "CODE_BOOLEAN":
							if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
								requirementValue = tenderingCriterionPropertyType.getExpectedCode().getValue();
							}else {
								requirementValue = "false";
							}
							break;
						case "WEIGHT_INDICATOR":
							if(tenderingCriterionPropertyType.getExpectedCode() != null) {        			
								requirementValue = Boolean.valueOf(tenderingCriterionPropertyType.getExpectedCode().getValue());
							}else {
								requirementValue = false;
							}
							break;        			
						default:
							break;
						}
						
					}else if("QUESTION".equals(tenderingCriterionPropertyType.getTypeCode().getValue())){
									
						HashMap<String, Object> values=findQuestion(tenderingCriterionPropertyType.getID().getValue(),questions,requirementById.getResponseType().getCode(),evidences,reqGroup);
						start=values.get("startDate");
						end=values.get("endDate");
						evidence=(String)values.get("evidence");
						if("1".equals(evidence)) {
							espdFieldName="availableElectronically";
						}
						requirementValue=values.get("requirementValue");
					}
				}
				if("0".equals(evidence) && requirementValue!= null) {
					log.info("		Property value: "+requirementValue);
					try {
						HashMap<String, String> tempVal = new HashMap<String, String>();
						tempVal=(HashMap<String, String>)requirementValue;
						if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d != null) {														
							d.put("url", tempVal.get("url"));
							d.put("code", tempVal.get("code"));	
							d.put("issuer", tempVal.get("issuer"));	
						} else{
							d=null;							
							PropertyUtils.setProperty(espdCriterion, "url", tempVal.get("url"));
							PropertyUtils.setProperty(espdCriterion, "code", tempVal.get("code"));		
							PropertyUtils.setProperty(espdCriterion, "issuer", tempVal.get("issuer"));		
						}
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						// TODO Auto-generated catch block
						log.error("si è verificato un errore", e);
					}
				}
				else if(start != null || end != null) {
					log.info("		Property value: "+start+"----"+end);
					try {
						if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d != null) {														
							d.put(espdFieldName, start);
							d.put(espdFieldName2, end);						
						} else{
							//d=null;							
							PropertyUtils.setProperty(espdCriterion, espdFieldName,new SimpleDateFormat("yyyy-MM-dd").parse(start.toString()));
							PropertyUtils.setProperty(espdCriterion, espdFieldName2,new SimpleDateFormat("yyyy-MM-dd").parse(end.toString()));														
						}
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
						// TODO Auto-generated catch block
						log.error("si è verificato un errore", e);
					}
				}else {					
					if(requirementValue != null) {
						log.info("		Property value: "+requirementValue);
						try {
							if(unbuonded && ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups() != null && d != null) {							
								if (requirementValue instanceof ExpectedAmountType) {
									// values which represent amounts are special and need to be stored in two fields
									d.put(espdFieldName, ((ExpectedAmountType) requirementValue).getValue());
									d.put(requirementById.getEspdCriterionFields().get(1), ((ExpectedAmountType) requirementValue).getCurrencyID());
								} else {
									if("lotId".equals(espdFieldName)) {
										ArrayList<String> lots = new ArrayList<String>();
										if((ArrayList<String>)d.get("lotId") != null) {										
											ArrayList<String> lot = (ArrayList<String>)d.get("lotId");
											lot.add((String)requirementValue);
											d.put(espdFieldName, lot);
										} else {
											if(requirementValue instanceof String) {
												lots.add((String)requirementValue);
												d.put(espdFieldName, lots);
											} else if(requirementValue instanceof HashMap){
												lots.add((String)((HashMap) requirementValue).get("lotId"));
												d.put(espdFieldName, lots);
											} else if (requirementValue instanceof List<?>) {
											    List<HashMap<String, String>> list = (List<HashMap<String, String>>) requirementValue;
											    if (!list.isEmpty() && list.get(0) instanceof HashMap<?, ?>) {
											    	for (HashMap<String, String> l : list) {											    		
											    		for (Entry<String, String> entry : l.entrySet()) {												    			
											    			String valore = entry.getValue();
											    	        lots.add(valore);
											    	    }
													}
											    	d.put(espdFieldName, lots);											        
											    }
											}
											
										}
									} else {
										d.put(espdFieldName, requirementValue);
									}
								}
								
								
							}else if(array) {
								List<HashMap<String,Object>> temp;
								if(PropertyUtils.getProperty(espdCriterion,espdFieldName) != null) {
									temp=(List<HashMap<String,Object>>) PropertyUtils.getProperty(espdCriterion,espdFieldName);
									
								}else {
									PropertyUtils.setProperty(espdCriterion, espdFieldName, new ArrayList<String>());
									temp=(List<HashMap<String,Object>>) PropertyUtils.getProperty(espdCriterion,espdFieldName);
								}
								HashMap<String,Object> arrObj = new HashMap<>();
								arrObj.put(espdFieldName, requirementValue);
								temp.add(arrObj);
								PropertyUtils.setProperty(espdCriterion, espdFieldName, temp);
								
							}				
							else{
								d=null;
								if (requirementValue instanceof ExpectedAmountType) {
									PropertyUtils.setProperty(espdCriterion, espdFieldName, ((ExpectedAmountType) requirementValue).getValue());
									PropertyUtils.setProperty(espdCriterion, requirementById.getEspdCriterionFields().get(1),((ExpectedAmountType) requirementValue).getCurrencyID());
									
								} else {
									
									PropertyUtils.setProperty(espdCriterion, espdFieldName, requirementValue);
									
								}
							}
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							log.error("errore in fase di generazione response ",e);
						}
					}
				}
			}
		}				
	}
    
    
    private HashMap<String, Object> findQuestion(String id,List<TenderingCriterionResponseType> questions,String type,List<EvidenceType> evidences,CacRequirementGroup reqGroup) {
    	
    	Iterator<TenderingCriterionResponseType> iter = questions.iterator();
    	HashMap<String, Object> values = new HashMap<String, Object>();
    	Object value= null;
    	Object startDate= null;
    	Object endDate= null;
    	String evidence=null;
    	while (iter.hasNext()) {
    		TenderingCriterionResponseType tenderingCriterionResponseType = iter.next();
    	//for (TenderingCriterionResponseType tenderingCriterionResponseType : questions) {
    		if(id.equals(tenderingCriterionResponseType.getValidatedCriterionPropertyID().getValue())) {
    			if(tenderingCriterionResponseType.getResponseValue() != null && tenderingCriterionResponseType.getResponseValue().size() > 0) {
    				
    				switch (type) {    			
    				case "DESCRIPTION":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getDescription().size() > 0) {    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getDescription().get(0).getValue();
    					}
    					
    					break;
    				case "LOT_IDENTIFIER":
    					if(tenderingCriterionResponseType.getResponseValue().size() > 0) {
    						ArrayList<HashMap<String, String>> lots=new ArrayList<HashMap<String, String>>();
    						for ( ResponseValueType a : tenderingCriterionResponseType.getResponseValue()) {
    							if(a != null && a.getResponseID() != null && a.getResponseID().getValue() != null) {    							
    								HashMap<String, String> temp = new HashMap<String, String>();
    								temp.put("lotId", a.getResponseID().getValue());    								
    								lots.add(temp);
    							}
							}
    						value=lots;
    					}
    					break;
    				case "ECONOMIC_OPERATOR_IDENTIFIER":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseID() != null) {
    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseID().getValue();
    					}
    					break;    				
    				case "URL":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseURI() != null) {    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseURI().getValue();
    					}
    					break;
    				case "AMOUNT":
    					value=new ExpectedAmountType();
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseAmount().getCurrencyID() != null) {    						    						
    						((ExpectedAmountType) value).setCurrencyID(tenderingCriterionResponseType.getResponseValue().get(0).getResponseAmount().getCurrencyID());
    					}
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseAmount() != null) {    						
    						((ExpectedAmountType) value).setValue(tenderingCriterionResponseType.getResponseValue().get(0).getResponseAmount().getValue());
    					}
    					break;
    				case "QUANTITY_INTEGER":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity() != null) {
    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity().getValue();
    					}
    					break;
    				case "QUANTITY_YEAR":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity() != null) {
    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity().getValue();
    					}
    					break;
    				case "CODE_COUNTRY":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseCode() != null) {
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseCode().getValue();
    					}
    					break;
    				case "CODE":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseCode() != null) {
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseCode().getValue();
    					}
    					break;    				
    				case "QUANTITY":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity() != null) {
    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity().getValue();
    					}
    					break;
    				case "PERCENTAGE":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity() != null) {
    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseQuantity().getValue();
    					}
    					break;
    				case "INDICATOR":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseIndicator() != null)
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getResponseIndicator().isValue();
    					break;
    				case "QUAL_IDENTIFIER":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getDescription().size() > 0) {    						
    						value=tenderingCriterionResponseType.getResponseValue().get(0).getDescription().get(0).getValue();
    					}
    					break;   
    				case "DATE":
    					if(tenderingCriterionResponseType.getResponseValue().get(0).getResponseDate() != null) {
    						
    						value=toDate(tenderingCriterionResponseType.getResponseValue().get(0).getResponseDate().getValue());
    					}
    					break;
    				default:
    					break;
    				}  
    			} else if ("PERIOD".equals(type)) {
    				if(tenderingCriterionResponseType.getApplicablePeriod() != null && tenderingCriterionResponseType.getApplicablePeriod().size() > 0) {        					
    					startDate=tenderingCriterionResponseType.getApplicablePeriod().get(0).getStartDate().getValue();
    					endDate=tenderingCriterionResponseType.getApplicablePeriod().get(0).getEndDate().getValue();	
    				}
    			} else if ("EVIDENCE_IDENTIFIER".equals(type)) {
    				if(tenderingCriterionResponseType.getEvidenceSupplied() != null && tenderingCriterionResponseType.getEvidenceSupplied().size() > 0) {    				
    					for (EvidenceType ev : evidences) {
							if(ev.getID().getValue().equals(tenderingCriterionResponseType.getEvidenceSupplied().get(0).getID().getValue())) {
								if(reqGroup.getId().startsWith("IT")) {
									evidence = "0";
									for(DocumentReferenceType drt : ev.getDocumentReference()) {
										HashMap<String, String> tempVal = new HashMap<String, String>();
										tempVal.put("code", drt.getID().getValue());
										tempVal.put("url", drt.getAttachment().getExternalReference().getURI().getValue());
										if(drt.getIssuerParty().getPartyName() != null && drt.getIssuerParty().getPartyName().size() > 0) {
											
											tempVal.put("issuer", drt.getIssuerParty().getPartyName().get(0).getName().getValue());
										}
										value=tempVal;
									}
								}else {
									List<AvailableElectronically> aeList=new ArrayList<AvailableElectronically>();
									evidence = "1";
									for(DocumentReferenceType drt : ev.getDocumentReference()) {										
										AvailableElectronically ae=new AvailableElectronically();
										if(drt.getID() != null) {										
											ae.setInfoElectronicallyCode(drt.getID().getValue());
										}
										if(drt.getIssuerParty().getPartyName() != null && drt.getIssuerParty().getPartyName().size() > 0 ) {
											ae.setInfoElectronicallyIssuer(drt.getIssuerParty().getPartyName().get(0).getName().getValue());
										}
										
										if(drt.getAttachment().getExternalReference() != null && drt.getAttachment().getExternalReference().getURI().getValue() != null) {										
											ae.setInfoElectronicallyUrl(drt.getAttachment().getExternalReference().getURI().getValue());
										}
										aeList.add(ae);
									}
									value=aeList;
								}
							}
						}
    				}
    			}
    			values.put("requirementValue", value);
    			values.put("startDate", startDate);
    			values.put("endDate", endDate);
    			values.put("evidence", evidence);
    			iter.remove();
    			return values;
    			
    		}
		}
    	return values;
    	
    	
    }
    
    
    
    public static Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
