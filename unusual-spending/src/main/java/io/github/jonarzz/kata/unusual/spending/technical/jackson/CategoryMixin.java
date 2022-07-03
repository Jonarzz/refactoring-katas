package io.github.jonarzz.kata.unusual.spending.technical.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.quarkus.jackson.JacksonMixin;

import java.io.IOException;

@JacksonMixin(Category.class)
@JsonDeserialize(using = CategoryMixin.CategoryDeserializer.class)
interface CategoryMixin {

    class CategoryDeserializer extends JsonDeserializer<Category> {

        @Override
        public Category deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return Category.named(jsonParser.getValueAsString());
        }
    }

}
