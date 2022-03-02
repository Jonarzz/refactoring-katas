package io.github.jonarzz.kata.banking.account.csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class CsvRow {

    private static final String DELIMITER = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private LocalDate timestamp;
    private int amount;

    private CsvRow(LocalDate timestamp, int amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    static CsvRow fromLine(String line) {
        var splitLine = line.split(DELIMITER);
        var timestamp = LocalDate.parse(splitLine[0], DATE_FORMATTER);
        var amount = Integer.parseInt(splitLine[1]);
        return new CsvRow(timestamp, amount);
    }

    static CsvRow fromValues(LocalDate timestamp, int amount) {
        return new CsvRow(timestamp, amount);
    }

    @Override
    public String toString() {
        return DATE_FORMATTER.format(timestamp) + DELIMITER + amount;
    }

    LocalDate timestamp() {
        return timestamp;
    }

    int amount() {
        return amount;
    }

}
