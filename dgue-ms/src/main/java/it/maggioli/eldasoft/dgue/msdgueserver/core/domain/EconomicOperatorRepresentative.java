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

import java.util.Date;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.Country;
import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
@Data
public class EconomicOperatorRepresentative {

    private String firstName;

    private String lastName;
    
    private String fiscalCode;

    private Date dateOfBirth;

    private String placeOfBirth;

    private String street;

    private String postalCode;

    private String city;

    private Country country;

    private String email;

    private String phone;

    /**
     * Position/Acting in the capacity of
     */
    private String position;

    /**
     * If needed, please provide detailed information on the representation (its forms, extent, purpose ...)
     */
    private String additionalInfo;
}
