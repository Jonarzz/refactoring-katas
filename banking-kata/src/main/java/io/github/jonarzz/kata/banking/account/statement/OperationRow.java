package io.github.jonarzz.kata.banking.account.statement;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class OperationRow extends BaseRow {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");

    private OperationRow(TemporalAccessor timestamp, int amount, long balance) {
        super(TIMESTAMP_FORMATTER.format(timestamp), amountAsString(amount), String.valueOf(balance));
    }

    public static OperationRow create(TemporalAccessor timestamp, int amount, long balance) {
        return new OperationRow(timestamp, amount, balance);
    }

    private static String amountAsString(int amount) {
        return (amount > 0 ? "+" : "")
               + amount;
    }

}
