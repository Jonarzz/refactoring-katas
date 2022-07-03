package io.github.jonarzz.kata.unusual.spending.technical.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
            var value = jsonParser.readValueAsTree();
            if (value != null && value.isValueNode()) {
                return Currency.getInstance(((ValueNode) value).asText());
            }
            return context.readValue(jsonParser, Currency.class);
        }
    }

}
