package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 10, 2020
 */
@Getter
public enum LegislationType {
    EU_REGULATION("EU_REGULATION", "European Regulation"),
    EU_DIRECTIVE("EU_DIRECTIVE", "European Directive"),
    EU_DECISION("EU_DECISION", "European Decision"),
    NATIONAL_LEGISLATION("NATIONAL_LEGISLATION", "National Legislation"),
    SUBNATIONAL_LEGISLATION("SUBNATIONAL_LEGISLATION", "Sub-national Legislation")
    ;

    private final String code;
    private final String name;

    LegislationType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
