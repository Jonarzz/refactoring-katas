package io.github.jonarzz.kata.banking.account.statement;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DataRow {

    private static final String TIMESTAMP_PATTERN = "d.M.yyyy";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    private String timestamp;
    private String amount;
    private String balance;

    public DataRow(TemporalAccessor timestamp, int amount, long balance) {
        this.timestamp = TIMESTAMP_FORMATTER.format(timestamp);
        this.amount = amountAsString(amount);
        this.balance = String.valueOf(balance);
    }

    String timestamp() {
        return timestamp;
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
