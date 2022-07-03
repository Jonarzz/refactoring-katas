package io.github.jonarzz.kata.unusual.spending.money;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

class CurrencyTest {

    @Test
    void formatUsd() {
        var valueToFormat = BigDecimal.valueOf(12.34d);

        var result = USD.format(valueToFormat);

        assertThat(result)
                .isEqualTo("$12.34");
    }

    @Nested
    class GetInstanceTest {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = "USD")
        void exists(String alphaCode) {
            var currency = Currency.getInstance(alphaCode);

            assertThat(currency)
                    .returns(alphaCode, Currency::alphaCode);
        }

        @Test
        void doesNotExist() {
            var alphaCode = "asdzxc";

            ThrowableAssert.ThrowingCallable testedMethod = () -> Currency.getInstance(alphaCode);

            assertThatThrownBy(testedMethod)
                    .hasMessage("Not found a currency instance for alpha code " + alphaCode);
        }
    }

}