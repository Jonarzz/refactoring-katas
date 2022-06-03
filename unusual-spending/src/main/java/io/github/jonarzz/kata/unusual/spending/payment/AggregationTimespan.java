package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.LocalTime.MAX;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class AggregationTimespan {

    private LocalDateTime start;
    private LocalDateTime end;

    private AggregationTimespan(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static AggregationTimespan between(LocalDateTime from, LocalDateTime to) {
        return new AggregationTimespan(from, to);
    }

    public static AggregationTimespan fromWhole(YearMonth month) {
        return between(month.atDay(1).atStartOfDay(),
                       month.atEndOfMonth().atTime(MAX));
    }

    LocalDateTime start() {
        return start;
    }

    LocalDateTime end() {
        return end;
    }

}
