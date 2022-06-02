package io.github.jonarzz.kata.unusual.spending.money;

import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class CostTest {

    @ParameterizedTest(name = "{0} + {0}")
    @MethodSource("addArgsSource")
    void add(Cost first, Cost second, Cost expectedResult) {
        var result = first.add(second);

        assertThat(result)
                .isEqualTo(expectedResult);
    }

    static Stream<Arguments> addArgsSource() {
        return Stream.of(
                arguments(usd(0, 0),   usd(0, 0),     usd(0, 0)),
                arguments(usd(1, 0),   usd(0, 0),     usd(1, 0)),
                arguments(usd(0, 0),   usd(1, 0),     usd(1, 0)),
                arguments(usd(1, 0),   usd(2, 0),     usd(3, 0)),
                arguments(usd(9, 0),   usd(13, 0),    usd(22, 0)),
                arguments(usd(1, 99),  usd(0, 59),    usd(2, 58)),
                arguments(usd(0, 999), usd(0, 1),     usd(10, 0)),
                arguments(usd(99, 99), usd(1299, 99), usd(1399, 98))
        );
    }

    @Nested
    class Negative {

        @Test
        void tryToCreatePriceWithNegativeDollarsValue() {
            assertThatThrownBy(() -> usd(-1, 0))
                    .hasMessage("Price cannot be a negative number");
        }

        @Test
        void tryToCreatePriceWithNegativeCentsValue() {
            assertThatThrownBy(() -> usd(0, -1))
                    .hasMessage("Price cannot be a negative number");
        }

    }

}