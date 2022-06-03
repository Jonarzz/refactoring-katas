package io.github.jonarzz.kata.unusual.spending.money;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

class CurrencyTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "USD, $12.34"
    })
    void format(Currency currency, String expectedValue) {
        var valueToFormat = BigDecimal.valueOf(12.34d);

        var result = currency.format(valueToFormat);

        assertThat(result)
                .isEqualTo(expectedValue);
    }

}