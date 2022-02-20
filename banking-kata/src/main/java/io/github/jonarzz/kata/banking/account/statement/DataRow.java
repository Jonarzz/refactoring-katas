package io.github.jonarzz.kata.banking.account.statement;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DataRow extends Row {

    private static final String TIMESTAMP_PATTERN = "d.M.yyyy";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
    static final int MAX_TIMESTAMP_LENGTH = TIMESTAMP_PATTERN.length() + 2; // two-digit day + two-digit month

    private String amount;
    private String balance;

    public DataRow(TemporalAccessor timestamp, int amount, long balance) {
        super(TIMESTAMP_FORMATTER.format(timestamp), amountAsString(amount), String.valueOf(balance));
        this.amount = amountAsString(amount);
        this.balance = String.valueOf(balance);
    }

    String amount() {
        return amount;
    }

    String balance() {
        return balance;
    }

    private static String amountAsString(int amount) {
        return (amount > 0 ? "+" : "")
               + amount;
    }

}
