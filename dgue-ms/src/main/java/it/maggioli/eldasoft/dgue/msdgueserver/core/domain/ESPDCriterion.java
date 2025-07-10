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

import java.util.ArrayList;
import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.intf.UnboundedRequirementGroup;
import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
@Data
public abstract class ESPDCriterion {

	protected List<AvailableElectronically> availableElectronically = new ArrayList<AvailableElectronically>();

    protected Boolean exists;
    
    protected Boolean infoElectronicallyAnswer;
    
    protected Boolean infoElectronicallyAnswer2;

    protected Boolean answer;
    
    protected Boolean compensatedDamage;
    
    protected Boolean compensatingDamage;
    
    protected Boolean concreteMeasures;
    
    protected String concreteMeasuresDescription;
    
    protected String code;
    
    protected String issuer;
    
    protected String url;
    
    protected AvailableElectronically availableElectronically2 = new AvailableElectronically();

    public boolean getExists() {
        return Boolean.TRUE.equals(exists);
    }

    public abstract Boolean getAnswer();
    
    public abstract Boolean getCompensatedDamage();
    
    public abstract Boolean getCompensatingDamage();
    
    public abstract Boolean getConcreteMeasures();
    
    public abstract Boolean getInfoElectronicallyAnswer();
    
    public abstract String getConcreteMeasuresDescription();
    
    public abstract String getCode();
    
    public abstract String getIssuer();
    
    public abstract String getUrl();

    public abstract List<AvailableElectronically> getAvailableElectronically();
    
    public abstract AvailableElectronically getAvailableElectronically2();

	

}
