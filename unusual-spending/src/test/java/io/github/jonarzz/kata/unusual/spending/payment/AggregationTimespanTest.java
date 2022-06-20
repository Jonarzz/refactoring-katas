package io.github.jonarzz.kata.unusual.spending.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.YearMonth;

class AggregationTimespanTest {

    @ParameterizedTest(name = "{0}-2022")
    @CsvSource({
            "2, 2022-02-01T00:00:00Z, 2022-02-28T23:59:59.999999999Z",
            "5, 2022-05-01T00:00:00Z, 2022-05-31T23:59:59.999999999Z",
            "6, 2022-06-01T00:00:00Z, 2022-06-30T23:59:59.999999999Z"
    })
    void monthly(int month, String expectedStart, String expectedEnd) {
        var timespan = AggregationTimespan.of(YearMonth.of(2022, month));

        assertThat(timespan.start())
                .isEqualTo(expectedStart);
        assertThat(timespan.end())
                .isEqualTo(expectedEnd);
    }

}