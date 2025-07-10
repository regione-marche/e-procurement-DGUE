package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Data
@Valid
public class ProcurementProjectLot {

    @NotEmpty
    private String numLot;
    @NotEmpty
    private String cigLot;
}
