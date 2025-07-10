package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.CacRequirementGroupImpl;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.BidType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CriterionElementType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Currency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.EvaluationMethodType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ResponseDataType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacLegislation;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcCriterionTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcExpectedResponse;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CbcResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseType;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
public final class CriteriaDeserializer extends JsonDeserializer<CriterionDefinitions> {

    private static final CriteriaDeserializer INSTANCE = new CriteriaDeserializer();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CriteriaDeserializer() {
    }
    
    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CriterionDefinitions.class, INSTANCE);
        MAPPER.registerModule(module);
    }

    static CriterionDefinitions parseJsonFile(String fileName) {
        try (InputStream is = new ClassPathResource("criteria/" + fileName).getInputStream()) {
            return MAPPER.readValue(is, CriterionDefinitions.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not read JSON file: '%s'.", fileName), e);
        }
    }

    @Override
	public CriterionDefinitions deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
         JsonNode node = jp.getCodec().readTree(jp);
         ArrayNode criteriaNode = (ArrayNode) node.get("criteria");

        final CriterionDefinitions criterionDefinitions = new CriterionDefinitions();

         for (JsonNode nd : criteriaNode) {
             CacCriterion crit = parseCacCriterion(nd);
             criterionDefinitions.addCriterion(crit.getUuid(), crit);
        	 System.out.println(nd.toString());
         }
//         addCcvEntityMappings(node, criterionDefinitions);

        return criterionDefinitions;
    }
    
    
	private CacCriterion parseCacCriterion(final JsonNode node) {
		if (nodeHasNoValues(node)) {
			return null;
		}
		
		
		final String name = parseStringNode("name", node);		
		final String uuid = parseStringNode("uuid", node);			
		final String description = parseStringNode("description", node);
		final String espdDocumentField = parseStringNode("espdDocumentField", node);
		final CbcCriterionTypeCode criterionType = parseCbcCriterionTypeCode("criterionType", node);
		final CacLegislation legislation = parseCacLegislation("legislationReference", node);
		final List<? extends CacRequirementGroup> groups = parseCacRequirementGroup("groups", node);
		final List<? extends CacCriterion> subcriteria = parseSubTenderingCriterion(node.get("subcriteria"));
		
		return new CacCriterion() {
			@Override
			public String getUuid() {
				return uuid;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public String getEspdDocumentField() {
				return espdDocumentField;
			}

			@Override
			public CbcCriterionTypeCode getCriterionType() {				
				return criterionType;
			}

			@Override
			public BigDecimal getWeight() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EvaluationMethodType getEvaluationMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getEvaluationMethodDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<? extends CacCriterion> getSubTenderingCriterion() {
				// TODO Auto-generated method stub
				return subcriteria;
			}

			@Override
			public CacLegislation getLegislation() {				
				return legislation;				
			}

			@Override
			public List<? extends CacRequirementGroup> getGroups() {				
				return groups;
			}
		};
	}
	
	private CacCriterion parseSubCacCriterion(final JsonNode node) {
		if (nodeHasNoValues(node)) {
			return null;
		}
		
		
		final String name = parseStringNode("name", node);
		final String uuid = parseStringNode("uuid", node);			
		final String description = parseStringNode("description", node);
		
		final List<? extends CacRequirementGroup> groups = parseCacRequirementGroup("groups", node);
	
		
		return new CacCriterion() {
			@Override
			public String getUuid() {
				return uuid;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public String getEspdDocumentField() {
				return null;
			}

			@Override
			public CbcCriterionTypeCode getCriterionType() {				
				return null;
			}

			@Override
			public BigDecimal getWeight() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EvaluationMethodType getEvaluationMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getEvaluationMethodDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<? extends CacCriterion> getSubTenderingCriterion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CacLegislation getLegislation() {				
				return null;				
			}

			@Override
			public List<? extends CacRequirementGroup> getGroups() {				
				return groups;
			}
		};
	}
	
	private List<CacCriterion> parseSubTenderingCriterion(JsonNode subcriteria) {
		if(subcriteria == null) {
			System.out.println("parseStringNode() JSON NODE [subcriteria] NOT FOUND");
			return null;
		}
		List<CacCriterion> sc=new ArrayList<CacCriterion>();
		CacCriterion c=parseSubCacCriterion(subcriteria);
		sc.add(c);
		return sc;
	}
	
	private static boolean nodeHasNoValues(JsonNode parentNode) {
		return parentNode == null || parentNode.size() <= 0;
	}
	
	private static String parseStringNode(String nodeName, JsonNode parentNode) {
		JsonNode n = parentNode.get(nodeName);
		if(n == null) {
			System.out.println("parseStringNode() JSON NODE [" + nodeName + "] NOT FOUND");
			return null;
		}
		return n.textValue();
	}
	
	private static List<String> parseListStringNode(String nodeName, JsonNode parentNode) {
		JsonNode n = parentNode.get(nodeName);
		List<String> list=new  ArrayList<String>();
		if(n == null) {
			System.out.println("parseStringNode() JSON NODE [" + nodeName + "] NOT FOUND");
			return null;
		}
		for (JsonNode node : n) {
			list.add(node.textValue());
		}
		
		return list;
	}
	
	private static CbcCriterionTypeCode parseCbcCriterionTypeCode(String nodeName, JsonNode parentNode) {
		JsonNode n = parentNode.get(nodeName);
		if(n == null) {
			System.out.println("parseStringNode() JSON NODE [" + nodeName + "] NOT FOUND");
		}
		final String espdType = parseStringNode("espdType", n);
		final String code = parseStringNode("code", n);
		return new CbcCriterionTypeCode() {
			
			@Override
			public String getEspdType() {				
				return espdType;
			}
			
			@Override
			public String getCode() {				
				return code;
			}
		};
		
	}

	private static CacLegislation parseCacLegislation(String nodeName, JsonNode parentNode) {
		JsonNode n = parentNode.get(nodeName);
		if(n == null) {
			System.out.println("parseStringNode() JSON NODE [" + nodeName + "] NOT FOUND");
		}
		final String url = parseStringNode("url", n);
		final String title = parseStringNode("title", n);		
		final String description = parseStringNode("description", n);
		final String article = parseStringNode("article", n);
		final String jurisdictionLevel = parseStringNode("jurisdictionLevel", n);
		
		return new CacLegislation() {
			
			@Override
			public String getUrl() {				
				return url;
			}
			
			@Override
			public String getTitle() {				
				return title;
			}
			
			@Override
			public String getJurisdictionLevel() {				
				return jurisdictionLevel;
			}
			
			@Override
			public String getDescription() {				
				return description;
			}
			
			@Override
			public String getArticle() {				
				return article;
			}
		};
		
	}

	
	
	private static List<? extends CacRequirementGroup> parseCacRequirementGroup(String nodeName, JsonNode parentNode) {
		JsonNode n = parentNode.get(nodeName);
		
		List<CacRequirementGroup> group=new ArrayList<CacRequirementGroup>();
		if(n == null) {
			System.out.println("parseStringNode() JSON NODE [" + nodeName + "] NOT FOUND");
			return null;
		}else {								
			for (final JsonNode objNode : n) {
				CacRequirementGroupImpl rg=new CacRequirementGroupImpl();
				
				final String name = parseStringNode("name", objNode);
				final String id = parseStringNode("id", objNode);
				final String propertyGroupTypeCode = parseStringNode("PropertyGroupTypeCode", objNode);
				final Boolean fulfillmentIndicator = Boolean.parseBoolean(parseStringNode("fulfillmentIndicator", objNode));
				final boolean unbounded = Boolean.parseBoolean(parseStringNode("unbounded", objNode));
				final boolean unbounded2 = Boolean.parseBoolean(parseStringNode("unbounded2", objNode));
				final boolean unbounded3 = Boolean.parseBoolean(parseStringNode("unbounded3", objNode));
				final boolean unboundedArray = Boolean.parseBoolean(parseStringNode("unboundedArray", objNode));
				final boolean array = Boolean.parseBoolean(parseStringNode("array", objNode));
				final List<? extends CacCriterionRequirement> requirements = parseCacCriterionRequirement("requirements", objNode);						
				final List<? extends CacRequirementGroup> subgroups=parseCacRequirementGroup("subgroups",objNode);
//						new ArrayList<CacRequirementGroupImpl>();
//				parseCacRequirementSubGroup("subgroups",objNode,subgroups);
					
				rg.setArray(array);
				rg.setPropertyGroupTypeCode(propertyGroupTypeCode);
				rg.setFulfillmentIndicator(fulfillmentIndicator);
				rg.setId(id);
				rg.setName(name);
				rg.setRequirements(requirements);
				rg.setSubgroups(subgroups);
				rg.setUnbounded(unbounded);
				rg.setUnbounded2(unbounded2);
				rg.setUnbounded3(unbounded3);
				rg.setUnboundedArray(unboundedArray);
				group.add(rg);
			}			
		}
				
		return group;
	}
	
	
	
	
	
	private static <T> List<? extends CacCriterionRequirement> parseCacCriterionRequirement(String nodeName, JsonNode parentNode){
		JsonNode n = parentNode.get(nodeName);
		List<CacCriterionRequirement> requirements=new ArrayList<CacCriterionRequirement>();
		if(n == null) {
			return null;
		}else {
			for (final JsonNode objNode : n) {
				final String description = parseStringNode("description", objNode);
				String identifier = parseStringNode("id", objNode);
				String idTmp = UUID.randomUUID().toString();
				if(identifier.startsWith("IT")) {					
					identifier = "IT-"+idTmp;
				} else {
					identifier = idTmp;
				}
				final String id = identifier;
				//final String id = parseStringNode("id", objNode) == null ? UUID.randomUUID().toString() : parseStringNode("id", objNode);
						
				final String responseType = parseStringNode("responseType", objNode);
				final String typeCode = parseStringNode("typeCode", objNode);
				final List<String> espdCriterionFields = parseListStringNode("espdCriterionFields", objNode);
				final String name = espdCriterionFields.get(0);		
				final String descriptionSearch = parseStringNode("descriptionSearch", objNode);
				
				CbcResponseType rs=new CbcResponseType<T>() {
									

					@Override
					public T parseValue(ResponseType responseType) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getCode() {						
						return responseType;
					}
				};
				
				CacCriterionRequirement cr=new CacCriterionRequirement() {
					
					@Override
					public CbcResponseType getResponseType() {						
						return rs;
					}
					
					@Override
					public String getName() {						
						return name;
					}
					
					@Override
					public String getId() {						
						return id;
					}
					
					@Override
					public CbcExpectedResponse getExpectedResponse() {
						// TODO Auto-generated method stub
						return new CbcExpectedResponse() {
							
							@Override
							public String getUnitCode() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public ResponseDataType getResponseDataType() {
								// TODO Auto-generated method stub
								return ResponseDataType.valueOf(rs.getCode());
							}
							
							@Override
							public BigDecimal getMinimumValueNumeric() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public BigDecimal getMaximumValueNumeric() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public BigDecimal getExpectedValueNumeric() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public BidType getExpectedCode() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public Currency getCurrency() {
								// TODO Auto-generated method stub
								return null;
							}
							
							@Override
							public CriterionElementType getCriterionElement() {		
								// TODO Auto-generated method stub
								return typeCode == null ? CriterionElementType.QUESTION : CriterionElementType.valueOf(typeCode);								
							}
							
							@Override
							public String getCertificationLevelDesc() {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public BigDecimal getAmount() {
								// TODO Auto-generated method stub
								return new BigDecimal(0);
							}
						};
					}
					
					@Override
					public List<String> getEspdCriterionFields() {
						// TODO Auto-generated method stub
						return espdCriterionFields;
					}
					
					@Override
					public String getDescription() {						
						return description;
					}

					@Override
					public String getDescriptionSearch() {
						return descriptionSearch;
					}

				};
				requirements.add(cr);
			}
		}
		return requirements;
	}
	
}
