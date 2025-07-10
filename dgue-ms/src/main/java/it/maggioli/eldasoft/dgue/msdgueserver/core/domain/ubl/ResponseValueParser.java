package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseType;

/**
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface ResponseValueParser<T> extends Serializable {

    T parseValue(ResponseType responseType);

}
