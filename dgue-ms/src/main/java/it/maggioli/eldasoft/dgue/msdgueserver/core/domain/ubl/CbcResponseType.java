package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

/**
 * Type of data expected in the response for a criterion requirement.
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CbcResponseType<T> extends ResponseValueParser<T> {

    String getCode();

}
