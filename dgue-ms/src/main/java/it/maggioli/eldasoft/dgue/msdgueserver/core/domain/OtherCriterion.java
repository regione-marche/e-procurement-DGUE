package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OtherCriterion extends ESPDCriterion implements UnboundedRequirementGroup{

	private String lotSubmit;
	private BigDecimal maxLot;
	private BigDecimal maxLotTender;
	private List<DynamicRequirementGroup> unboundedGroups = new ArrayList<>();
	
	private Boolean selectionCriteria;
	private String name;
	private String electronicCertificate;
	private String reference;
	private Boolean certificate;
	
	private String economicOperator;
	private String role;

	private String description1;
	private String description2;
	private String description3;
	private String description4;
	private String description5;
	private BigDecimal doubleValue1;
	
	private Boolean answer2;
	private Boolean booleanValue1;
	private Boolean booleanValue2; // is not applicable (not used anymore)
	private Boolean booleanValue3; // e) Will the economic operator... indicator
	private Boolean booleanValue4;
	private Boolean booleanValue5;
	
	private BigDecimal employeeQuantity;
	private BigDecimal valueAmount;
	private String currency;
	
	private List<HashMap<String, String>> lotId;

	public OtherCriterion() {
		// !! award criteria should always exist (be present in a ESPD Response)
		setExists(true);
	}
	
	@Override
	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OtherCriterion build() {
		return new OtherCriterion();
	}

	
	public Boolean getBooleanValue1() {
		return Boolean.TRUE.equals(this.booleanValue1);
	}

	public Boolean getBooleanValue3() {
		return Boolean.TRUE.equals(this.booleanValue3);
	}

    @Override
    public Boolean getAnswer() {
	    if (this.answer == null) {
		    // other (economic operator) criterion with no answer has a default value of FALSE
		    return Boolean.FALSE;
	    }
	    return this.answer;
    }

    @Override
	public List<DynamicRequirementGroup> getUnboundedGroups() {		
		return unboundedGroups;
	}
    
    @Override
  	public Boolean getCompensatedDamage() {
      	if (this.compensatedDamage == null) {
              // exclusion criteria with no answer have a default value of FALSE
              return Boolean.FALSE;
          }
          return this.compensatedDamage;
  	}

  	@Override
  	public Boolean getCompensatingDamage() {
  		if (this.compensatingDamage == null) {
              // exclusion criteria with no answer have a default value of FALSE
              return Boolean.FALSE;
          }
          return this.compensatingDamage;
  	}

  	@Override
  	public Boolean getConcreteMeasures() {
  		if (this.concreteMeasures == null) {
              // exclusion criteria with no answer have a default value of FALSE
              return Boolean.FALSE;
          }
          return this.concreteMeasures;
  	}

  	@Override
  	public String getConcreteMeasuresDescription() {
  		
          return this.concreteMeasuresDescription;
  	}

  	@Override
  	public List<AvailableElectronically> getAvailableElectronically() {
  		// TODO Auto-generated method stub
  		return availableElectronically;
  	}

  	@Override
  	public AvailableElectronically getAvailableElectronically2() {
  		// TODO Auto-generated method stub
  		return availableElectronically2;
  	}
	
  	@Override
	public Boolean getInfoElectronicallyAnswer() {
		// TODO Auto-generated method stub
		return infoElectronicallyAnswer;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}

	@Override
	public String getIssuer() {
		// TODO Auto-generated method stub
		return issuer;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return url;
	}
    
}
