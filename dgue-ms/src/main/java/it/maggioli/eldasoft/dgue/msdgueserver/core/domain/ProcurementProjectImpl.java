package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.CPV;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.ProjectType;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacProcurementProject;
import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Data
public class ProcurementProjectImpl implements CacProcurementProject {

    private String name;
    private String description;
    private ProjectType type;
    private List<CPV> cpv;
}
