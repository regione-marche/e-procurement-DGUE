package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import com.fasterxml.jackson.annotation.JsonValue;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Getter
public enum CPV implements CodeList {

    CPV_03000000_1("03000000-1", "Agricultural, farming, fishing, forestry and related products", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03100000_2("03100000-2", "Agricultural and horticultural products", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03110000_5("03110000-5", "Crops, products of market gardening and horticulture", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111000_2("03111000-2", "Seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111100_3("03111100-3", "Soya beans", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111200_4("03111200-4", "Peanuts", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111300_5("03111300-5", "Sunflower seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111400_6("03111400-6", "Cotton seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111500_7("03111500-7", "Sesame seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111600_8("03111600-8", "Mustard seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111700_9("03111700-9", "Vegetable seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111800_0("03111800-0", "Fruit seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03111900_1("03111900-1", "Flower seeds", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03112000_9("03112000-9", "Unmanufactured tobacco", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03113000_6("03113000-6", "Plants used for sugar manufacturing", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03113100_7("03113100-7", "Sugar beet", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03113200_8("03113200-8", "Sugar cane", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03114000_3("03114000-3", "Straw and forage", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03114100_4("03114100-4", "Straw", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini"),
    CPV_03114200_5("03114200-5", "Forage", "Prodotti dell'agricoltura, dell'allevamento, della pesca, della silvicoltura e prodotti affini");

    private final String code;
    private final String name;
    private final String translation;

    CPV(String code, String name, String translation) {
        this.code = code;
        this.name = name;
        this.translation = translation;
    }

    @Override
    public String getListVersionId() {
        return "20080817";
    }

    @Override
    public String getListId() {
        return "CPVCodes";
    }

    public String getListAgencyName() {
        return Agency.EU_COM_OP.getIdentifier();
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }
}
