package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.YearMonth.of;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimespanTest {

    @ParameterizedTest(name = "{0}-2022")
    @CsvSource({
            "2, 2022-02-01T00:00:00, 2022-02-28T23:59:59.999999999",
            "5, 2022-05-01T00:00:00, 2022-05-31T23:59:59.999999999",
            "6, 2022-06-01T00:00:00, 2022-06-30T23:59:59.999999999"
    })
    void monthly(int month, String expectedStart, String expectedEnd) {
        var timespan = Timespan.from(of(2022, month));

        assertThat(timespan.from())
                .isEqualTo(expectedStart);
        assertThat(timespan.to())
                .isEqualTo(expectedEnd);
    }

}