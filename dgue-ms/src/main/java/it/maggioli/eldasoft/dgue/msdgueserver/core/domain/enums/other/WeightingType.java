package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import java.util.Optional;
import java.util.stream.Stream;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 04, 2020
 */
@Getter
public enum WeightingType implements CodeList {

    PERCENTAGE("PERCENTAGE", "Percentage", "Percentuale"),
    NUMERIC("NUMERIC", "Numeric", "Valore numerico")
    ;

    private final String code;
    private final String name;
    private final String translation;

    WeightingType(String code, String name, String translation) {
        this.code = code;
        this.name = name;
        this.translation = translation;
    }

    /**
     *
     * @param code
     * @return
     */
    public static Optional<WeightingType> getWeightingTypeByCode(final String code) {
        return Stream.of(WeightingType.values())
                .filter(weightingType -> code.equals(weightingType.getCode()))
                .findFirst();
    }

    /**
     *
     * @return
     */
    @Override
    public String getListVersionId() {
        return "2.1.0";
    }

    /**
     *
     * @return
     */
    @Override
    public String getListId() {
        return "WeightingType";
    }

    /**
     *
     * @return
     */
    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }


}
