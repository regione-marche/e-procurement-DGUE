package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.Date;

import lombok.Data;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Data
public class ESPDRequestMetadata {

    private String id;

    private String issueDate;

    private String uuid;

    private String issueTime;
}
