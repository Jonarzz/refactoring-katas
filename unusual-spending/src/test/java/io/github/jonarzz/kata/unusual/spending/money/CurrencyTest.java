package io.github.jonarzz.kata.unusual.spending.money;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CurrencyTest {

    @Test
    void formatUsd() {
        var valueToFormat = BigDecimal.valueOf(12.34d);

        var result = USD.format(valueToFormat);

        assertThat(result)
                .isEqualTo("$12.34");
    }

}