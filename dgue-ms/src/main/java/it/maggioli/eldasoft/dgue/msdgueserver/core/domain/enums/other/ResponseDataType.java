package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
@Getter
public enum ResponseDataType implements CodeList {
    AMOUNT("AMOUNT", "An Amount, and currency, as defined in UN/CEFACT's CCTS"),
    CODE("CODE", "A code for an unespecified concept"),
    CODE_COUNTRY("CODE_COUNTRY", "Code identifying a country (Compulsory use of ISO 3166-1 alpha 2"),
    DATE("DATE", "Date values (format YYYY-DD-MM)"),
    DESCRIPTION("DESCRIPTION", "Textual field"),
    EVIDENCE_IDENTIFIER("EVIDENCE_IDENTIFIER", "Points at the instance of an evidence"),
    INDICATOR("INDICATOR", "'true' or 'false' values representing 'Yes' affirmative or 'No' negative answers"),
    PERCENTAGE("PERCENTAGE", "A decimal number representing a percent (e.g. 0.1)"),
    PERIOD("PERIOD", "Period object as defined in UBL-2.1"),
    QUANTITY_INTEGER("QUANTITY_INTEGER", "A number representing a quantity in a specific unit of measure. The unit has to be specified (e.g. Kg)."),
    QUANTITY_YEAR("QUANTITY_YEAR", "A non-negative integer representing a year. The unit has to be specified as YEAR"),
    QUANTITY("QUANTITY", "A number representing a generic quantity, integer or decimal, with no unit specified."),
    NONE("NONE", "No response is expected. This may be the case when a contracting authority wants to set specific requirements (e.g. requirements on lots) and the answer is calculated out of the responses to other criteria."),
    IDENTIFIER("IDENTIFIER", "An identifier for a person, entity or object (e.g. a Party, a Lot, etc.)"),
    URL("URL", "A url, including e-mail addresses"),
    EVIDENCE_URL("EVIDENCE_URL", "A url, including e-mail addresses"),
    MAXIMUM_AMOUNT("MAXIMUM_AMOUNT", "The maximum amount the answer must have"),
    MINIMUM_AMOUNT("MINIMUM_AMOUNT", "The minimum amount the answer must have"),
    MAXIMUM_VALUE_NUMERIC("MAXIMUM_VALUE_NUMERIC", "The maximum value the answer to a criterion question must have"),
    MINIMUM_VALUE_NUMERIC("MINIMUM_VALUE_NUMERIC", "The minimum value the answer to a criterion question must have"),
    TRANSLATION_TYPE_CODE("TRANSLATION_TYPE_CODE", "The type of transation that a contracting authority's requirement shall be translated for example certified translation"),
    CERTIFICATION_LEVEL_DESCRIPTION("CERTIFICATION_LEVEL_DESCRIPTION", "The description of the level of the expected certification that a contracting authority requires to an economic operator in a tendering process"),
    COPY_QUALITY_TYPE_CODE("COPY_QUALITY_TYPE_CODE", "The type of Copy quality required by a contracting authority, expressed as a code"),
    TIME("TIME", "Time values (format HH:MM:SS)"),
    LOT_IDENTIFIER("LOT_IDENTIFIER", "The identifier of a procurement procedure lot"),
    WEIGHT_INDICATOR("WEIGHT_INDICATOR", "Establishes whether a criterion is weighted or not."),
    CODE_BOOLEAN("CODE_BOOLEAN", "Code identifying a type of GUI control element."),
    ECONOMIC_OPERATOR_IDENTIFIER("ECONOMIC_OPERATOR_IDENTIFIER", "Code used to detect an identifier referred to an Economic Operator"),
	QUAL_IDENTIFIER("QUAL_IDENTIFIER","Code identifying a Qualification number.");
	
    private final String code;
    private final String name;

    ResponseDataType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListVersionId() {
        return "2.1.1";
    }

    @Override
    public String getListId() {
        return "ResponseDataType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
