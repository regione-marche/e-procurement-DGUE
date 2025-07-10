package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml;

import java.util.HashMap;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 18, 2020
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "namespaces")
public class DgueNamespaceMapper extends NamespacePrefixMapper {

    @Setter
    @Getter
    private HashMap<String, String> map = new HashMap<>();

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (map.containsKey(namespaceUri))
            return map.get(namespaceUri);
        return suggestion;
    }
}
