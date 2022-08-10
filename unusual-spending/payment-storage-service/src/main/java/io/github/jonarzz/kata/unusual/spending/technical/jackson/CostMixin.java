package io.github.jonarzz.kata.unusual.spending.technical.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.quarkus.jackson.JacksonMixin;

import java.math.BigDecimal;

@JacksonMixin(Cost.class)
abstract class CostMixin {

    Currency currency;
    BigDecimal amount;

    @JsonCreator
    private CostMixin(@JsonProperty("currency") Currency currency,
                      @JsonProperty("amount") BigDecimal amount) {
    }
}
