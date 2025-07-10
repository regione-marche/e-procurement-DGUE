package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import lombok.Data;

@Data
public class CacRequirementGroupImpl implements CacRequirementGroup{

	private String id;
	  
	private String name;

	private Boolean fulfillmentIndicator;

	private List<? extends CacCriterionRequirement> requirements;

	private List<? extends CacRequirementGroup> subgroups;
	
	private boolean unbounded;
	
	private boolean unbounded2;
	
	private boolean unbounded3;
	
	private boolean unboundedArray;
	
	private boolean array;

	private String propertyGroupTypeCode;

	

	

	
	  
	  
}
