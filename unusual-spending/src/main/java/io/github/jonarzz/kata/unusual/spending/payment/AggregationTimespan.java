package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.LocalTime.MAX;
import static java.time.ZoneOffset.UTC;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

public class AggregationTimespan {

    private OffsetDateTime start;
    private OffsetDateTime end;

    private AggregationTimespan(OffsetDateTime start, OffsetDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static AggregationTimespan between(OffsetDateTime from, OffsetDateTime to) {
        return new AggregationTimespan(from, to);
    }

    public static AggregationTimespan of(YearMonth month, ZoneOffset offset) {
        return between(month.atDay(1)
                            .atStartOfDay()
                            .atOffset(offset),
                       month.atEndOfMonth()
                            .atTime(MAX)
                            .atOffset(offset));
    }

    public static AggregationTimespan of(YearMonth month) {
        return of(month, UTC);
    }

    @Override
    public String toString() {
        return "AggregationTimespan{start=%s, end=%s}".formatted(start, end);
    }

    OffsetDateTime start() {
        return start;
    }

    OffsetDateTime end() {
        return end;
    }

}
