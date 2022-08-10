package io.github.jonarzz.kata.unusual.spending.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.YearMonth;

class AggregationTimespanTest {

    @ParameterizedTest(name = "{0}-{1}")
    @CsvSource({
            "2020, 2, 2020-02-01, 2020-02-29",
            "2022, 2, 2022-02-01, 2022-02-28",
            "2022, 5, 2022-05-01, 2022-05-31",
            "2022, 6, 2022-06-01, 2022-06-30",
            "2025, 8, 2025-08-01, 2025-08-31"
    })
    void monthly(int year, int month, String expectedStart, String expectedEnd) {
        var timespan = AggregationTimespan.of(YearMonth.of(year, month));

        assertThat(timespan.start())
                .isEqualTo(expectedStart);
        assertThat(timespan.end())
                .isEqualTo(expectedEnd);
    }

}