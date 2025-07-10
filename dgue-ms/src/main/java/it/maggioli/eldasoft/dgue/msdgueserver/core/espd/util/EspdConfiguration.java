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

package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.util;

import java.util.Arrays;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
@Data
@Component
public class EspdConfiguration {

    @Value("${info.build.version:N/A}")
    private String buildVersion;

    @Value("${espd.exchange-model.version:2.2.1}")
    private String exchangeModelVersion;

    @Value("${last.build.date:N/A}")
    private String lastBuildDate;

    @Value("${espd.environment:false}")
    private boolean espdEnvironment;

	@Value("${apache.fop.xml.configuration.location:classpath:grow/fop/fop-config.xml}")
	private String fopXmlConfigurationLocation;

	@Value("${apache.fop.defaultBaseUri:.}")
	private String fopDefaultBaseUri;

    private final Environment environment;

	@Autowired
    EspdConfiguration(Environment environment) {
		this.environment = environment;
	}

	public String getActiveProfile() {
        if (environment.getActiveProfiles() != null && environment.getActiveProfiles().length > 0) {
            return Arrays.toString(environment.getActiveProfiles());
        }
        return Arrays.toString(environment.getDefaultProfiles());
    }
}
