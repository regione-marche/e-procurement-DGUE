package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CPV;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ProjectType;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
public interface CacProcurementProject {

    String getName();
    String getDescription();
    ProjectType getType();
    List<CPV> getCpv();
}
