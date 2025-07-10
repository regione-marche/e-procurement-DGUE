package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class ExclusionCriterion extends ESPDCriterion {
    @Override
    public Boolean getAnswer() {
        if (this.answer == null) {
            // exclusion criteria with no answer have a default value of FALSE
            return Boolean.FALSE;
        }
        return this.answer;
    }
    
    @Override
	public Boolean getCompensatedDamage() {
    	
        return this.compensatedDamage;
	}

	@Override
	public Boolean getCompensatingDamage() {
		
        return this.compensatingDamage;
	}

	@Override
	public Boolean getConcreteMeasures() {
		
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
