package io.github.jonarzz.kata.unusual.spending.payment;

import java.time.LocalDate;
import java.time.YearMonth;

public class AggregationTimespan {

    private LocalDate start;
    private LocalDate end;

    private AggregationTimespan(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public static AggregationTimespan between(LocalDate from, LocalDate to) {
        return new AggregationTimespan(from, to);
    }

    public static AggregationTimespan of(YearMonth month) {
        return between(month.atDay(1),
                       month.atEndOfMonth());
    }

    @Override
    public String toString() {
        return "AggregationTimespan{start=%s, end=%s}".formatted(start, end);
    }

    LocalDate start() {
        return start;
    }

    LocalDate end() {
        return end;
    }

}
