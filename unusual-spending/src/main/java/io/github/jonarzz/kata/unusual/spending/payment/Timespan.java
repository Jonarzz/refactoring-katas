package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.LocalTime.MAX;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class Timespan {

    private LocalDateTime from;
    private LocalDateTime to;

    private Timespan(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public static Timespan between(LocalDateTime from, LocalDateTime to) {
        return new Timespan(from, to);
    }

    public static Timespan from(YearMonth month) {
        return between(month.atDay(1).atStartOfDay(),
                       month.atEndOfMonth().atTime(MAX));
    }

    LocalDateTime from() {
        return from;
    }

    LocalDateTime to() {
        return to;
    }

}
