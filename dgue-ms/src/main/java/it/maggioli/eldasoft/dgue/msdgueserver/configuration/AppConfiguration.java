package it.maggioli.eldasoft.dgue.msdgueserver.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 04, 2019
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({"it.maggioli.eldasoft.dgue.msdgueserver"})
public class AppConfiguration {
}
