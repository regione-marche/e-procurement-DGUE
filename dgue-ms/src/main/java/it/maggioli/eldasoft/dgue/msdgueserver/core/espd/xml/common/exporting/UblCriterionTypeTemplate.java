/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.maggioli.eldasoft.dgue.msdgueserver.controllers.ESPDRequestController;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AvailableElectronically;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.DynamicRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria.CriterionType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Agency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.utils.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.EvidenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LegislationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyGroupType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TenderingCriterionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ArticleType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ConfidentialityLevelCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CriterionTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EvaluationMethodTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssuerIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.JurisdictionLevelType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PropertyGroupTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TitleType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.URIType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.WeightNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.WeightingConsiderationDescriptionType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;

/**
 * Creates a UBL {@link TenderingCriterionType} from the information coming from ESPD.
 * <p/>
 * A {@link TenderingCriterionType} can have many {@link TenderingCriterionPropertyType} and {@link TenderingCriterionPropertyGroupType} elements.
 * A {@link TenderingCriterionPropertyGroupType} can in turn have other subgroups and requirements of its own.
 * <p/>
 * <p>
 * Certain requirement groups can be virtually unlimited in number but they always start from the definition of a
 * primary group. Their structure is thus cloned but their responses will vary according to the user input.
 * <p>
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
@Slf4j
public abstract class UblCriterionTypeTemplate {

	private final UblRequirementTypeTemplate ublRequirementTransformerTemplate;
	
	protected UblCriterionTypeTemplate() {
		this.ublRequirementTransformerTemplate = buildRequirementTransformer();
	}

	/**
	 * Creates a UBL {@link TenderingCriterionType} from the ESPD criteria.
	 *
	 * @param saFiscalCode fiscal code or vat number of the SA
	 * @param cacCriterion  The meta information concerning a criterion
	 * @param espdCriterion The criterion holding the user values
	 *
	 * @return A {@link TenderingCriterionType} containing all the information coming from an user of the ESPD application.
	 */	
	TenderingCriterionType buildCriterionType(final String saFiscalCode, CacCriterion cacCriterion, ESPDCriterion espdCriterion,
			QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		final TenderingCriterionType criterionType = new TenderingCriterionType();

		addCriterionID(cacCriterion, criterionType);
		addTypeCode(cacCriterion, criterionType);
		addName(cacCriterion, criterionType);
		addDescription(cacCriterion, criterionType);		
		addWeightAndWeightingConsideration(cacCriterion, criterionType);
		addEvaluationMethod(cacCriterion, criterionType);
		addLegislationReference(cacCriterion, criterionType);		
		addGroups(saFiscalCode, cacCriterion, espdCriterion, criterionType,responseType,alsoResponse,tcr);				
		addSubcriteria(saFiscalCode,cacCriterion, espdCriterion,criterionType,responseType,alsoResponse,tcr);
		
		return criterionType;
	}
	
	private void addSubcriteria(final String saFiscalCode, CacCriterion cacCriterion, ESPDCriterion espdCriterion, 
			TenderingCriterionType criterionType,QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		
		if(cacCriterion.getSubTenderingCriterion() != null) {
		
			for (CacCriterion sc : cacCriterion.getSubTenderingCriterion()) {
				if (sc != null) {				
					TenderingCriterionType subC=new TenderingCriterionType();
					
					IDType idType = CommonUblFactory.buildIdTypeValue(sc.getUuid());
					idType.setSchemeAgencyID(Agency.EU_COM_GROW.getIdentifier());		
					idType.setSchemeID("CriteriaTaxonomy");
					idType.setSchemeVersionID(CriterionType.OTHER.getListVersionId());												
					subC.setID(idType);
					
					NameType nt=new NameType();
					nt.setValue(sc.getName());
					subC.setName(nt);
					
					DescriptionType dt=new DescriptionType();
					dt.setValue(sc.getDescription());
					subC.getDescription().add(dt);
					
					addGroups(saFiscalCode, sc, espdCriterion, subC,responseType,alsoResponse,tcr);
					
					criterionType.getSubTenderingCriterion().add(subC);
				}
			}
		}		
	}

	

	private void addCriterionID(CacCriterion input, TenderingCriterionType criterionType) {
		final IDType idType = CommonUblFactory.buildIdTypeValue(input.getUuid());
		idType.setSchemeAgencyID(Agency.EU_COM_GROW.getIdentifier());		
		//idType.setSchemeID("CriteriaTaxonomy");
		idType.setSchemeVersionID(CriterionType.OTHER.getListVersionId());
		criterionType.setID(idType);
	}

	private void addTypeCode(CacCriterion input, TenderingCriterionType criterionType) {
		if (input.getCriterionType() == null) {
			return;
		}
		final CriterionTypeCodeType criterionTypeCodeType = new CriterionTypeCodeType();
		criterionTypeCodeType.setValue(input.getCriterionType().getCode());
		criterionTypeCodeType.setListID(CriterionType.OTHER.getListId());
		criterionTypeCodeType.setListAgencyName(CriterionType.OTHER.getListAgencyName());
		criterionTypeCodeType.setListAgencyID(CriterionType.OTHER.getListAgencyId());
		criterionTypeCodeType.setListVersionID(CriterionType.OTHER.getListVersionId());
		criterionType.setCriterionTypeCode(criterionTypeCodeType);
	}

	private void addName(CacCriterion input, TenderingCriterionType criterionType) {
		final NameType nameType = new NameType();
		nameType.setValue(input.getName());
		criterionType.setName(nameType);
	}

	private void addDescription(CacCriterion input, TenderingCriterionType criterionType) {
		final DescriptionType descriptionType = new DescriptionType();
		descriptionType.setValue(input.getDescription());
		criterionType.getDescription().add(descriptionType);
	}
		

	private void addWeightAndWeightingConsideration(CacCriterion input, TenderingCriterionType criterionType) {
		if (input.getWeight() != null) {
			final WeightNumericType weightNumericType = new WeightNumericType();
			weightNumericType.setValue(input.getWeight());
			criterionType.setWeightNumeric(weightNumericType);
		}

		if (isNotBlank(input.getEvaluationMethodDescription())) {
			final WeightingConsiderationDescriptionType weightingConsiderationDescriptionType = new WeightingConsiderationDescriptionType();
			weightingConsiderationDescriptionType.setValue(input.getEvaluationMethodDescription());
			criterionType.getWeightingConsiderationDescription().add(weightingConsiderationDescriptionType);
		}
	}

	private void addEvaluationMethod(CacCriterion input, TenderingCriterionType criterionType) {
		if (input.getEvaluationMethod() != null) {
			final EvaluationMethodTypeCodeType evaluationMethodTypeCodeType = new EvaluationMethodTypeCodeType();
			evaluationMethodTypeCodeType.setValue(input.getEvaluationMethod().getCode());
			evaluationMethodTypeCodeType.setListID(input.getEvaluationMethod().getListId());
			evaluationMethodTypeCodeType.setListAgencyName(input.getEvaluationMethod().getListAgencyName());
			evaluationMethodTypeCodeType.setListVersionID(input.getEvaluationMethod().getListVersionId());
			criterionType.setEvaluationMethodTypeCode(evaluationMethodTypeCodeType);
		}
	}

	private void addLegislationReference(CacCriterion input, TenderingCriterionType criterionType) {
		if (input.getLegislation() == null) {
			return;
		}

		final LegislationType legislationType = new LegislationType();

		final TitleType title = new TitleType();
		title.setValue(input.getLegislation().getTitle());
		legislationType.getTitle().add(title);

		final DescriptionType description = new DescriptionType();
		description.setValue(input.getLegislation().getDescription());
		legislationType.getDescription().add(description);

		final JurisdictionLevelType jurisdictionLevelType = new JurisdictionLevelType();
		jurisdictionLevelType.setValue(input.getLegislation().getJurisdictionLevel());
		legislationType.getJurisdictionLevel().add(jurisdictionLevelType);

		final ArticleType article = new ArticleType();
		article.setValue(input.getLegislation().getArticle());
		legislationType.getArticle().add(article);

		final URIType uriType = new URIType();
		uriType.setValue(input.getLegislation().getUrl());
		legislationType.getURI().add(uriType);

		criterionType.getLegislation().add(legislationType);
	}
	


	
	
	private void addGroups(final String saFiscalCode, CacCriterion cacCriterion, ESPDCriterion espdCriterion, TenderingCriterionType criterionType,
			QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		if (isEmpty(cacCriterion.getGroups())) {
			return;
		}
	
		List<TenderingCriterionPropertyGroupType> groupTypes = new ArrayList<>(cacCriterion.getGroups().size() + 1);
		for (CacRequirementGroup group : cacCriterion.getGroups()) {
			if (group.isUnbounded() && espdCriterion instanceof UnboundedRequirementGroup) {
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion)
						.getUnboundedGroups();
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					groupTypes.add(buildGroupType(cacCriterion,saFiscalCode, group, espdCriterion,null, 0,responseType,alsoResponse,tcr));
				} else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int groupIndex = 0; groupIndex < unboundedGroups.size(); groupIndex++) {
						groupTypes.add(buildGroupType(cacCriterion,saFiscalCode, group, espdCriterion,unboundedGroups.get(groupIndex), groupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else {		
				// just fill in the information for a normal requirement group
				groupTypes.add(buildGroupType(cacCriterion,saFiscalCode, group, espdCriterion,null, -1,responseType,alsoResponse,tcr));
			}
		}
		criterionType.getTenderingCriterionPropertyGroup().addAll(groupTypes);
		
	}

	private TenderingCriterionPropertyGroupType buildGroupType( CacCriterion cacCriterion,final String saFiscalCode, CacRequirementGroup requirementGroup,
															   ESPDCriterion espdCriterion,DynamicRequirementGroup dynamicRequirementGroup,
												int groupIndex,QualificationApplicationResponseType responseType,boolean alsoResponse
												,List<TenderingCriterionResponseType> tcr) {
		final TenderingCriterionPropertyGroupType groupType = new TenderingCriterionPropertyGroupType();

		addGroupId(requirementGroup, groupType);
		/*
		if (ccvGroup.fulfillmentIndicator() != null) {
			groupType.setPi("GROUP_FULFILLED.ON_" + String.valueOf(ccvGroup.fulfillmentIndicator()).toUpperCase());
		}*/
		addPropertyGroupTypeCodeType(cacCriterion,requirementGroup, groupType);
		addRequirements(cacCriterion,saFiscalCode, requirementGroup, espdCriterion, groupType,dynamicRequirementGroup, groupIndex,responseType,alsoResponse,tcr);
		addSubGroups(cacCriterion,saFiscalCode, requirementGroup, espdCriterion, groupType,dynamicRequirementGroup, groupIndex,responseType,alsoResponse,tcr);		
		return groupType;
	}
	
	
	
	
	
	private TenderingCriterionPropertyGroupType buildGroupTypeArray(CacCriterion cacCriterion,final String saFiscalCode, CacRequirementGroup requirementGroup,
			   ESPDCriterion espdCriterion,QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		final TenderingCriterionPropertyGroupType groupType = new TenderingCriterionPropertyGroupType();
		
		addGroupId(requirementGroup, groupType);		
		addPropertyGroupTypeCodeType(cacCriterion,requirementGroup, groupType);
		addRequirementsArray(saFiscalCode, requirementGroup, espdCriterion, groupType,tcr);
		addSubGroups(cacCriterion,saFiscalCode, requirementGroup, espdCriterion, groupType,null, -1,responseType,alsoResponse,tcr);		
		return groupType;
	}

	
	private void addPropertyGroupTypeCodeType(CacCriterion cacCriterion,CacRequirementGroup input, TenderingCriterionPropertyGroupType groupType) {
		final PropertyGroupTypeCodeType propertyGroupTypeCodeType = new PropertyGroupTypeCodeType();
		propertyGroupTypeCodeType.setValue(input.getPropertyGroupTypeCode());
		propertyGroupTypeCodeType.setListID("PropertyGroupType");
		propertyGroupTypeCodeType.setListAgencyID(Agency.EU_COM_GROW.getIdentifier());
		propertyGroupTypeCodeType.setListVersionID(CriterionType.OTHER.getListVersionId());
		groupType.setPropertyGroupTypeCode(propertyGroupTypeCodeType);
	}
	
	private void addGroupId(CacRequirementGroup input, TenderingCriterionPropertyGroupType groupType) {
		IDType idType = CommonUblFactory.buildIdTypeValue(input.getId());
		idType.setSchemeID("ISO / IEC 9834-8:2008");
		idType.setSchemeAgencyID(Agency.EU_COM_GROW.getIdentifier());
		idType.setSchemeVersionID(CriterionType.OTHER.getListVersionId());
		// idType.setValue(input.getId());
		groupType.setID(idType);
	}

	private void addRequirements(CacCriterion cacCriterion,final String saFiscalCode, CacRequirementGroup group, ESPDCriterion espdCriterion, TenderingCriterionPropertyGroupType groupType,
			DynamicRequirementGroup dynamicRequirementGroup,int groupIndex,QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		if (isEmpty(group.getRequirements())) {
			return;
		}				
		List<TenderingCriterionPropertyType> requirementTypes = new ArrayList<>(group.getRequirements().size() + 1);
		for (CacCriterionRequirement req : group.getRequirements()) {		
						
			
				
			if(alsoResponse) {
				
				String evidenceCode = "";
				if(req.getResponseType().getCode().equals("EVIDENCE_IDENTIFIER") && req.getEspdCriterionFields().get(0).equals("evidence")) {
					if((espdCriterion!= null && espdCriterion.getInfoElectronicallyAnswer() != null && espdCriterion.getInfoElectronicallyAnswer()) ||
					   (dynamicRequirementGroup!= null && dynamicRequirementGroup.get("infoElectronicallyAnswer") != null && 
					   (boolean)dynamicRequirementGroup.get("infoElectronicallyAnswer") == true)) {
						
						EvidenceType et=new EvidenceType();
						EvidenceType etTemp=new EvidenceType();
						if(responseType.getEvidence().size() > 0) {					
							etTemp=responseType.getEvidence().get(responseType.getEvidence().size()-1);
						}else {
							etTemp = null;
						}
						IDType idt=new IDType();
						idt.setSchemeID("ISO / IEC 9834-8:2008");
						idt.setSchemeVersionID("4");
						idt.setSchemeAgencyID("EU-COM-GROW");
						if(etTemp == null) {
							evidenceCode = "EVIDENCE-1";
						}else {
							String[] parts = etTemp.getID().getValue().split("-");						
							String part2 = parts[1];
							Long l=Long.valueOf(part2);
							l++;
							evidenceCode = "EVIDENCE-"+l;
						}
						idt.setValue(evidenceCode);
						et.setID(idt);
						ConfidentialityLevelCodeType clct=new ConfidentialityLevelCodeType();
						clct.setListID("ConfidentialityLevel");
						clct.setListAgencyID("EU-COM-GROW");
						clct.setListVersionID("2.1.1");						
						clct.setValue("CONFIDENTIAL");
						//clct.setListID("ConfidentialityLevel");
						//clct.setListID("2.1.1");
						et.setConfidentialityLevelCode(clct);
						List<DocumentReferenceType> docList=new ArrayList<DocumentReferenceType>();
						List<AvailableElectronically> availableElectronically=new ArrayList<AvailableElectronically>();
						boolean modify=false;
						if(group.getId().startsWith("IT")) {
							
							if(espdCriterion.getCode() != null || espdCriterion.getUrl() != null || espdCriterion.getIssuer() != null) {
								
								DocumentReferenceType drt=new DocumentReferenceType();
								IDType id=new IDType();
								id.setSchemeAgencyID("EU-COM-GROW");
								if(espdCriterion.getCode() != null) {							
									id.setValue(espdCriterion.getCode());
								}
								
								AttachmentType at=new AttachmentType();
								ExternalReferenceType ert=new ExternalReferenceType();
								URIType uri=new URIType();
								if(espdCriterion.getUrl() != null) {		
									uri.setValue(espdCriterion.getUrl());
									ert.setURI(uri);
									at.setExternalReference(ert);
								}
								
								PartyType issuerParty=new PartyType();
								PartyNameType pnt=new PartyNameType();
								NameType nt=new NameType();
								if(espdCriterion.getIssuer() != null) {
									nt.setValue(espdCriterion.getIssuer());
								}
								pnt.setName(nt);
								
								issuerParty.getPartyName().add(pnt);
								drt.setIssuerParty(issuerParty);
								drt.setID(id);
								drt.setAttachment(at);
								
								docList.add(drt);
								modify=true;
							}else {
								if(dynamicRequirementGroup!= null && (dynamicRequirementGroup.get("code") != null
										|| dynamicRequirementGroup.get("issuer") != null
										|| dynamicRequirementGroup.get("url") != null)) {							
									
								
														
									DocumentReferenceType drt=new DocumentReferenceType();
									IDType id=new IDType();
									id.setSchemeAgencyID("EU-COM-GROW");
									if(dynamicRequirementGroup.get("code") != null) {							
										id.setValue((String)dynamicRequirementGroup.get("code"));
									}
									
									AttachmentType at=new AttachmentType();
									ExternalReferenceType ert=new ExternalReferenceType();
									URIType uri=new URIType();
									if(dynamicRequirementGroup.get("url") != null) {		
										uri.setValue((String)dynamicRequirementGroup.get("url"));
										ert.setURI(uri);
										at.setExternalReference(ert);
									}
									
									PartyType issuerParty=new PartyType();
									PartyNameType pnt=new PartyNameType();
									NameType nt=new NameType();
									if(dynamicRequirementGroup.get("issuer") != null) {
										nt.setValue((String)dynamicRequirementGroup.get("issuer"));
									}
									pnt.setName(nt);
									
									issuerParty.getPartyName().add(pnt);
									drt.setIssuerParty(issuerParty);
									drt.setID(id);
									drt.setAttachment(at);
									
								
									
									docList.add(drt);
									modify=true;
								}
							}
						}else{					
							
							if(espdCriterion.getAvailableElectronically() != null && espdCriterion.getAvailableElectronically().size() > 0) {							
								availableElectronically = espdCriterion.getAvailableElectronically();
							}
							if(dynamicRequirementGroup!= null && dynamicRequirementGroup.get("availableElectronically") != null) {							
								//availableElectronically = (List<AvailableElectronically>)dynamicRequirementGroup.get("availableElectronically");
								ObjectMapper mapper = new ObjectMapper();
								availableElectronically = mapper.convertValue(dynamicRequirementGroup.get("availableElectronically"), new TypeReference<List<AvailableElectronically>>() { });
							}
							
							for (AvailableElectronically ae : availableElectronically) {						
								DocumentReferenceType drt=new DocumentReferenceType();
								IDType id=new IDType();
								id.setSchemeAgencyID("EU-COM-GROW");
								if(ae.getInfoElectronicallyCode() != null) {							
									id.setValue(ae.getInfoElectronicallyCode());
								}
								
								AttachmentType at=new AttachmentType();
								ExternalReferenceType ert=new ExternalReferenceType();
								URIType uri=new URIType();
								if(ae.getInfoElectronicallyUrl() != null) {		
									uri.setValue(ae.getInfoElectronicallyUrl());
									ert.setURI(uri);
									at.setExternalReference(ert);
								}
								
								PartyType issuerParty=new PartyType();
								PartyNameType pnt=new PartyNameType();
								NameType nt=new NameType();
								if(ae.getInfoElectronicallyIssuer() != null) {
									nt.setValue(ae.getInfoElectronicallyIssuer());
								}
								pnt.setName(nt);
								
								issuerParty.getPartyName().add(pnt);
								drt.setIssuerParty(issuerParty);
								drt.setID(id);
								drt.setAttachment(at);
								
								docList.add(drt);
								modify=true;
							}
						}
						if(modify) {						
							et.getDocumentReference().addAll(docList);
							responseType.getEvidence().add(et);
						}
						modify=false;
					}
				} else if (req.getResponseType().getCode().equals("EVIDENCE_IDENTIFIER") && req.getEspdCriterionFields().get(0).equals("infoElectronicallyAnswer2")) {

					if((espdCriterion!= null && (espdCriterion.getUrl() != null || espdCriterion.getCode() != null || espdCriterion.getIssuer() != null)) ||
					   (dynamicRequirementGroup!= null && (dynamicRequirementGroup.get("url") != null || 
					   dynamicRequirementGroup.get("code") != null || 
					   dynamicRequirementGroup.get("issuer") != null))) {
						
						EvidenceType et=new EvidenceType();
						EvidenceType etTemp=new EvidenceType();
						if(responseType.getEvidence().size() > 0) {					
							etTemp=responseType.getEvidence().get(responseType.getEvidence().size()-1);
						}else {
							etTemp = null;
						}
						IDType idt=new IDType();
						idt.setSchemeID("ISO / IEC 9834-8:2008");
						idt.setSchemeVersionID("4");
						idt.setSchemeAgencyID("EU-COM-GROW");
						if(etTemp == null) {
							evidenceCode = "EVIDENCE-1";
						}else {
							String[] parts = etTemp.getID().getValue().split("-");						
							String part2 = parts[1];
							Long l=Long.valueOf(part2);
							l++;
							evidenceCode = "EVIDENCE-"+l;
						}
						idt.setValue(evidenceCode);
						et.setID(idt);
						ConfidentialityLevelCodeType clct=new ConfidentialityLevelCodeType();
						clct.setListID("ConfidentialityLevel");
						clct.setListAgencyID("EU-COM-GROW");
						clct.setListVersionID("2.1.1");						
						clct.setValue("CONFIDENTIAL");
						//clct.setListID("ConfidentialityLevel");
						//clct.setListID("2.1.1");
						et.setConfidentialityLevelCode(clct);
						List<DocumentReferenceType> docList=new ArrayList<DocumentReferenceType>();
						List<AvailableElectronically> availableElectronically=new ArrayList<AvailableElectronically>();
						boolean modify=false;
						if(group.getId().startsWith("IT")) {
							
							if(espdCriterion.getCode() != null || espdCriterion.getUrl() != null || espdCriterion.getIssuer() != null) {
								
								DocumentReferenceType drt=new DocumentReferenceType();
								IDType id=new IDType();
								id.setSchemeAgencyID("EU-COM-GROW");
								if(espdCriterion.getCode() != null) {							
									id.setValue(espdCriterion.getCode());
								}
								
								AttachmentType at=new AttachmentType();
								ExternalReferenceType ert=new ExternalReferenceType();
								URIType uri=new URIType();
								if(espdCriterion.getUrl() != null) {		
									uri.setValue(espdCriterion.getUrl());
									ert.setURI(uri);
									at.setExternalReference(ert);
								}
								
								PartyType issuerParty=new PartyType();
								PartyNameType pnt=new PartyNameType();
								NameType nt=new NameType();
								if(espdCriterion.getIssuer() != null) {
									nt.setValue(espdCriterion.getIssuer());
								}
								pnt.setName(nt);
								
								issuerParty.getPartyName().add(pnt);
								drt.setIssuerParty(issuerParty);
								drt.setID(id);
								drt.setAttachment(at);
								
								docList.add(drt);
								modify=true;
							}else {
								if(dynamicRequirementGroup!= null && (dynamicRequirementGroup.get("code") != null
										|| dynamicRequirementGroup.get("issuer") != null
										|| dynamicRequirementGroup.get("url") != null)) {							
									
								
														
									DocumentReferenceType drt=new DocumentReferenceType();
									IDType id=new IDType();
									id.setSchemeAgencyID("EU-COM-GROW");
									if(dynamicRequirementGroup.get("code") != null) {							
										id.setValue((String)dynamicRequirementGroup.get("code"));
									}
									
									AttachmentType at=new AttachmentType();
									ExternalReferenceType ert=new ExternalReferenceType();
									URIType uri=new URIType();
									if(dynamicRequirementGroup.get("url") != null) {		
										uri.setValue((String)dynamicRequirementGroup.get("url"));
										ert.setURI(uri);
										at.setExternalReference(ert);
									}
									
									PartyType issuerParty=new PartyType();
									PartyNameType pnt=new PartyNameType();
									NameType nt=new NameType();
									if(dynamicRequirementGroup.get("issuer") != null) {
										nt.setValue((String)dynamicRequirementGroup.get("issuer"));
									}
									pnt.setName(nt);
									
									issuerParty.getPartyName().add(pnt);
									drt.setIssuerParty(issuerParty);
									drt.setID(id);
									drt.setAttachment(at);
									
								
									
									docList.add(drt);
									modify=true;
								}
							}
						}else{					
							
							if(espdCriterion.getAvailableElectronically() != null && espdCriterion.getAvailableElectronically().size() > 0) {							
								availableElectronically = espdCriterion.getAvailableElectronically();
							}
							if(dynamicRequirementGroup!= null && dynamicRequirementGroup.get("availableElectronically") != null) {							
								//availableElectronically = (List<AvailableElectronically>)dynamicRequirementGroup.get("availableElectronically");
								ObjectMapper mapper = new ObjectMapper();
								availableElectronically = mapper.convertValue(dynamicRequirementGroup.get("availableElectronically"), new TypeReference<List<AvailableElectronically>>() { });
							}
							
							for (AvailableElectronically ae : availableElectronically) {						
								DocumentReferenceType drt=new DocumentReferenceType();
								IDType id=new IDType();
								id.setSchemeAgencyID("EU-COM-GROW");
								if(ae.getInfoElectronicallyCode() != null) {							
									id.setValue(ae.getInfoElectronicallyCode());
								}
								
								AttachmentType at=new AttachmentType();
								ExternalReferenceType ert=new ExternalReferenceType();
								URIType uri=new URIType();
								if(ae.getInfoElectronicallyUrl() != null) {		
									uri.setValue(ae.getInfoElectronicallyUrl());
									ert.setURI(uri);
									at.setExternalReference(ert);
								}
								
								PartyType issuerParty=new PartyType();
								PartyNameType pnt=new PartyNameType();
								NameType nt=new NameType();
								if(ae.getInfoElectronicallyIssuer() != null) {
									nt.setValue(ae.getInfoElectronicallyIssuer());
								}
								pnt.setName(nt);
								
								issuerParty.getPartyName().add(pnt);
								drt.setIssuerParty(issuerParty);
								drt.setID(id);
								drt.setAttachment(at);
								
								docList.add(drt);
								modify=true;
							}
						}
						if(modify) {						
							et.getDocumentReference().addAll(docList);
							responseType.getEvidence().add(et);
						}
						modify=false;
					}				
				}
					
				//passo il codice della evidence al metodo buildRequirementTypeResponse, nel metodo controllo se c'è e lo associo a quella appena creata
				if(group.isUnboundedArray()) {
					List<String> lotti = new ArrayList<String>();
					if(dynamicRequirementGroup.get(req.getName()) instanceof String) {						
						lotti = new ArrayList<String>(Arrays.asList(((String)dynamicRequirementGroup.get(req.getName())).split(",")));
					} else {
						lotti = (ArrayList<String>)dynamicRequirementGroup.get(req.getName());
					}
					if(lotti == null) {
						lotti = new ArrayList<String>();
					}
					for (String s : lotti) {
						DynamicRequirementGroup d = new DynamicRequirementGroup();
						d.put(req.getName(), s);
						requirementTypes.add(ublRequirementTransformerTemplate.buildRequirementType(saFiscalCode, req, espdCriterion, group,d, groupIndex,evidenceCode,tcr));
					}
				} else {				
					requirementTypes.add(ublRequirementTransformerTemplate.buildRequirementType(saFiscalCode, req, espdCriterion, group,dynamicRequirementGroup, groupIndex,evidenceCode,tcr));
				}
				
				 
			}else {
				if(group.isUnboundedArray()) {					
					List<String> lotti = new ArrayList<String>();
					if(dynamicRequirementGroup.get(req.getName()) instanceof String) {						
						lotti = new ArrayList<String>(Arrays.asList(((String)dynamicRequirementGroup.get(req.getName())).split(",")));
					} else {
						lotti = (ArrayList<String>)dynamicRequirementGroup.get(req.getName());
					}
					if(lotti == null) {
						lotti = new ArrayList<String>();
					}
					for (String s : lotti) {
						DynamicRequirementGroup d = new DynamicRequirementGroup();
						d.put(req.getName(), s);
						requirementTypes.add(ublRequirementTransformerTemplate.buildRequirementType(saFiscalCode, req, espdCriterion, group,d, groupIndex,"",null));
					}
					
				
				} else {					
					requirementTypes.add(ublRequirementTransformerTemplate.buildRequirementType(saFiscalCode, req, espdCriterion, group,dynamicRequirementGroup, groupIndex,"",null));
				}
			}
		}		
		groupType.getTenderingCriterionProperty().addAll(requirementTypes);
	}
	
	private void addRequirementsArray(final String saFiscalCode, CacRequirementGroup group, ESPDCriterion espdCriterion,
			TenderingCriterionPropertyGroupType groupType,List<TenderingCriterionResponseType> tcr) {
		if (isEmpty(group.getRequirements())) {
			return;
		}				
		List<TenderingCriterionPropertyType> requirementTypes = new ArrayList<>(group.getRequirements().size() + 1);
		for (CacCriterionRequirement req : group.getRequirements()) {	
			try {
				List<HashMap<String, Object>> temp= (List<HashMap<String, Object>>)PropertyUtils.getProperty(espdCriterion, req.getEspdCriterionFields().get(0));
				if(temp != null) {
				
					for (HashMap<String, Object> map : temp) {
						System.out.println(map);
						DynamicRequirementGroup dynamicRequirementGroup=new DynamicRequirementGroup(); 
						dynamicRequirementGroup.put(req.getEspdCriterionFields().get(0), map.get(req.getEspdCriterionFields().get(0)));
						requirementTypes.add(ublRequirementTransformerTemplate.buildRequirementType(saFiscalCode, req, espdCriterion, group,dynamicRequirementGroup,-1,"",tcr));
					}
				}
			} catch (InvocationTargetException | IllegalAccessException | IntrospectionException e) {
				// TODO Auto-generated catch block
				log.error("Sie è verificato un errore nel metodo: addRequirementsArray", e);
			}
//			
		}		
		groupType.getTenderingCriterionProperty().addAll(requirementTypes);
	}
	
	

	private void addSubGroups(CacCriterion cacCriterion,final String saFiscalCode, CacRequirementGroup requirementGroup, 
			ESPDCriterion espdCriterion, TenderingCriterionPropertyGroupType parentGroup, DynamicRequirementGroup dynamicRequirementGroup,
			int groupIndex,QualificationApplicationResponseType responseType,boolean alsoResponse,List<TenderingCriterionResponseType> tcr) {
		if (isEmpty(requirementGroup.getSubgroups())) {
			return;
		}

		List<TenderingCriterionPropertyGroupType> subGroups = new ArrayList<>(parentGroup.getTenderingCriterionProperty().size() + 1);
		for (CacRequirementGroup cacRequirementGroup : requirementGroup.getSubgroups()) {
			if((cacCriterion.getUuid().equals("3aaca389-4a7b-406b-a4b9-080845d127e7") || cacCriterion.getUuid().equals("c599c130-b29f-461e-a187-4e16c7d40db7")) && cacRequirementGroup.getName().equals("G1.1.1.1")) {
				System.out.println("unboundedGroups");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				if (dynamicRequirementGroup != null && !isEmpty((List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups"))) {
					
					List<DynamicRequirementGroup> unboundedGroupsTemp=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=new ArrayList<DynamicRequirementGroup>();
					ObjectMapper mapper = new ObjectMapper();

					unboundedGroups = mapper.convertValue(unboundedGroupsTemp, new TypeReference<List<DynamicRequirementGroup>>() { });					
				}
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if((cacCriterion.getUuid().equals("3aaca389-4a7b-406b-a4b9-080845d127e7") || cacCriterion.getUuid().equals("c599c130-b29f-461e-a187-4e16c7d40db7")) && cacRequirementGroup.getName().equals("G1.1.2.1")) {
				System.out.println("unboundedGroups2");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				if (dynamicRequirementGroup != null && !isEmpty((List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2"))) {
					
					List<DynamicRequirementGroup> unboundedGroupsTemp=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2");
					unboundedGroups=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2");
					unboundedGroups=new ArrayList<DynamicRequirementGroup>();
					ObjectMapper mapper = new ObjectMapper();

					unboundedGroups = mapper.convertValue(unboundedGroupsTemp, new TypeReference<List<DynamicRequirementGroup>>() { });					
				}
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if(cacCriterion.getUuid().equals("ab0e7f2e-6418-40e2-8870-6713123e41ad") && cacRequirementGroup.getName().equals("G1.2.1")) {
				System.out.println("unboundedGroups");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				if (dynamicRequirementGroup != null && !isEmpty((List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups"))) {
					
					List<DynamicRequirementGroup> unboundedGroupsTemp=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=new ArrayList<DynamicRequirementGroup>();
					ObjectMapper mapper = new ObjectMapper();

					unboundedGroups = mapper.convertValue(unboundedGroupsTemp, new TypeReference<List<DynamicRequirementGroup>>() { });					
				}
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if(cacCriterion.getUuid().equals("ab0e7f2e-6418-40e2-8870-6713123e41ad") && cacRequirementGroup.getName().equals("G1.2.2")) {
				System.out.println("unboundedGroups2");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				if (dynamicRequirementGroup != null && !isEmpty((List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2"))) {
					
					List<DynamicRequirementGroup> unboundedGroupsTemp=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2");
					unboundedGroups=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups2");
					unboundedGroups=new ArrayList<DynamicRequirementGroup>();
					ObjectMapper mapper = new ObjectMapper();

					unboundedGroups = mapper.convertValue(unboundedGroupsTemp, new TypeReference<List<DynamicRequirementGroup>>() { });					
				}
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if(cacCriterion.getUuid().equals("63adb07d-db1b-4ef0-a14e-a99785cf8cf6") && cacRequirementGroup.getName().equals("G1.1.1")) {
				System.out.println("unboundedGroups");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if(cacCriterion.getUuid().equals("63adb07d-db1b-4ef0-a14e-a99785cf8cf6") && cacRequirementGroup.getName().equals("G1.2.1")) {
				System.out.println("unboundedGroups2");
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups2();
				
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				}else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if ((cacRequirementGroup.isUnbounded() && espdCriterion instanceof UnboundedRequirementGroup)) {
				List<DynamicRequirementGroup> unboundedGroups = ((UnboundedRequirementGroup) espdCriterion).getUnboundedGroups();
				
				if (dynamicRequirementGroup != null && !isEmpty((List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups"))) {
					
					List<DynamicRequirementGroup> unboundedGroupsTemp=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=(List<DynamicRequirementGroup>)dynamicRequirementGroup.get("unboundedGroups");
					unboundedGroups=new ArrayList<DynamicRequirementGroup>();
					ObjectMapper mapper = new ObjectMapper();

					unboundedGroups = mapper.convertValue(unboundedGroupsTemp, new TypeReference<List<DynamicRequirementGroup>>() { });					
				}
				if (isEmpty(unboundedGroups)) {
					// if the user did not add values we still need to hold at least the structure of the primary group
					subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, 0,responseType,alsoResponse,tcr));
				} else {
					// we just clone the unbounded groups as needed and fill them with the information coming from the users
					for (int subGroupIndex = 0; subGroupIndex < unboundedGroups.size(); subGroupIndex++) {
						subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,unboundedGroups.get(subGroupIndex), subGroupIndex,responseType,alsoResponse,tcr));
					}
				}
			} else if(cacRequirementGroup.isArray()){				
					subGroups.add(buildGroupTypeArray(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,responseType,alsoResponse,tcr));								
			} else {
				subGroups.add(buildGroupType(cacCriterion,saFiscalCode, cacRequirementGroup, espdCriterion,dynamicRequirementGroup, groupIndex,responseType,alsoResponse,tcr));
			}
		}
		parentGroup.getSubsidiaryTenderingCriterionPropertyGroup().addAll(subGroups);
	}
	


	/**
	 * @return An instance of a class capable of creating {@link TenderingCriterionPropertyType}.
	 */
	protected abstract UblRequirementTypeTemplate buildRequirementTransformer();

}
