package io.github.jonarzz.kata.unusual.spending.technical.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.quarkus.jackson.JacksonMixin;

import java.io.IOException;

@JacksonMixin(Currency.class)
@JsonDeserialize(using = CurrencyMixin.CurrencyDeserializer.class)
interface CurrencyMixin {

    class CurrencyDeserializer extends JsonDeserializer<Currency> {

        @Override
        public Currency deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            var node = jsonParser.readValueAsTree();
            if (node instanceof ValueNode valueNode) {
                return Currency.getInstance(valueNode.asText());
            }
            if (node instanceof ObjectNode objectNode) {
                return Currency.create(getValue(objectNode, "alphaCode"),
                                       getValue(objectNode, "languageTag"));
            }
            return null;
        }

        private String getValue(ObjectNode objectNode, String property) {
            return objectNode.findValuesAsText(property)
                             .stream()
                             .findFirst()
                             .orElseThrow(() -> new IllegalArgumentException("Property '" + property
                                                                             + "' is required for the Currency object"));
        }
    }

}
