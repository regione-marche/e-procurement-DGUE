package it.maggioli.eldasoft.dgue.msdgueserver.configuration;

import javax.xml.bind.Marshaller;
import java.util.HashMap;
import java.util.Map;

import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.DgueNamespaceMapper;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationrequest_2.QualificationApplicationRequestType;
import oasis.names.specification.ubl.schema.xsd.qualificationapplicationresponse_2.QualificationApplicationResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Feb 28, 2020
 */
@Configuration
@Slf4j
public class JaxbConfiguration {
    @Autowired
    private final DgueNamespaceMapper namespaceMapper = null;

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        final Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(QualificationApplicationRequestType.class.getPackage().getName(),
                QualificationApplicationResponseType.class.getPackage().getName());
        final Map<String, Object> map = new HashMap<>(2);
        map.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        map.put("com.sun.xml.bind.namespacePrefixMapper", namespaceMapper);
        jaxb2Marshaller.setMarshallerProperties(map);
        return jaxb2Marshaller;
    }

}
