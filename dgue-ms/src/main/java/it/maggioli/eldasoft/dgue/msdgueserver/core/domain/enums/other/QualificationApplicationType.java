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
public enum QualificationApplicationType implements CodeList {

    REGULATED("Regulated", "Regulated", "Regolata"),
    SELFCONTAINED("SELFCONTAINED", "Self-contained", "Autoconsistente"),
    EXTENDED("EXTENDED","extended","estesa");

    private final String code;
    private final String name;
    private final String translation;

    QualificationApplicationType(final String code, final String name, final String translation) {
        this.code = code;
        this.name = name;
        this.translation = translation;
    }

    /**
     *
     * @param code
     * @return
     */
    public static Optional<QualificationApplicationType> getQualificationApplicationTypeByCode(final String code) {
        return Stream.of(QualificationApplicationType.values())
                .filter(qualificationApplicationType -> code.equals(qualificationApplicationType.getCode()))
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
        return "QualificationApplicationType";
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
