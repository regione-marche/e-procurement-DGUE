package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCountry;

public class MyEnumDeserializer extends JsonDeserializer<Country> {
    @Override
    public Country deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if(node != null) {        
        	String iso2Code = node.get("name").asText();
        	return Stream.of(Country.values())
        			.filter(enumValue -> enumValue.getIso2Code().equals(iso2Code))
        			.findFirst()
        			.orElseThrow(() -> new IllegalArgumentException("iso2Code "+iso2Code+" is not recognized"));
        }
        return null;
    }
}
