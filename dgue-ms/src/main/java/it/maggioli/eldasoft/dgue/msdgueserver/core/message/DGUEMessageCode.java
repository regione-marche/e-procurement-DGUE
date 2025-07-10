package it.maggioli.eldasoft.dgue.msdgueserver.core.message;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Dec 05, 2019
 */
public enum DGUEMessageCode {
    // 500
    INTERNAL_SERVER_ERROR(500),
    // 5XX
    RESPONSE_GENERIC_ERROR(501),

    // 400
    PARAM_MUST_BE_NOT_NULL_ERROR(401),
    INVALID_PARAMETERS(402), // if there is one or more parameters not valid
    INPUT_VALIDATION_ERROR(403),
    RESPONSE_RESOURCE_NOT_FOUND(404), // for APIs that return resource detail
    RESOURCE_ALREADY_EXISTS(405),
    UNAUTHORIZED(406),
    RESPONSE_FORBIDDEN(407),
    INVALID_BODY(408);

    DGUEMessageCode(long code) {
        this.code = code;
    }

    private long code;

    public long getCode() {
        return this.code;
    }

    public static DGUEMessageCode byName(String name) {
        for (DGUEMessageCode v : values()) {
            if (v.name().equals(name)) {
                return v;
            }
        }
        return null;
    }
}
