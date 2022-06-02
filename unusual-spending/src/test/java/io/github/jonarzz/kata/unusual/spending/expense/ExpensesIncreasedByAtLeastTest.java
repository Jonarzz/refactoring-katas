package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdValue.percentage;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

class ExpensesIncreasedByAtLeastTest {

    @Nested
    @DisplayName("250% increase threshold")
    class _250percentIncreaseThreshold {

        SpendingThreshold threshold = new ExpensesIncreasedByAtLeast(percentage(250));

        @ParameterizedTest(name = "previous = {0}, current = {1}")
        @CsvSource({
                "100.50, 101.25",
                "2, 4.99",
                "200.01, 500",
                "2001.77, 5004.41"
        })
        void thresholdNotReached(BigDecimal previous, BigDecimal current) {
            var result = threshold.thresholdReached(previous, current);

            assertThat(result)
                    .isFalse();
        }

        @ParameterizedTest(name = "previous = {0}, current = {1}")
        @CsvSource({
                "2, 5",
                "2, 5.1",
                "10.77, 27",
                "200.10, 500.30",
                "1000.50, 3000.77",
                "2001.77, 5004.42"
        })
        void thresholdReached(BigDecimal previous, BigDecimal current) {
            var result = threshold.thresholdReached(previous, current);

            assertThat(result)
                    .isTrue();
        }

    }
}