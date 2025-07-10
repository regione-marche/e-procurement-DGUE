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

package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.criteria;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Agency;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
public enum CriterionType implements CodeList {

    EXCLUSION,
    SELECTION,
    OTHER;

    @Override
    public String getListVersionId() {
        return "2.1.1";
    }

    @Override
    public String getListId() {
        return "CriteriaTypeCode";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getLongName();
    }
    
    public String getListAgencyId() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
