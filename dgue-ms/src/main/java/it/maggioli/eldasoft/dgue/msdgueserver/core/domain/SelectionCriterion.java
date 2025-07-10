/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
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

package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class SelectionCriterion extends ESPDCriterion {

    private String description;

    @Override
    public Boolean getAnswer() {
        if (this.answer == null) {
            // selection criteria with no answer have a default value of TRUE
            return Boolean.TRUE;
        }
        return this.answer;
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
