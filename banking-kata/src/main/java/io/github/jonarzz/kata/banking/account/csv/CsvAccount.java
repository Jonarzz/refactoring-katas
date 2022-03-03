package io.github.jonarzz.kata.banking.account.csv;

import static java.nio.file.StandardOpenOption.APPEND;

import io.github.jonarzz.kata.banking.account.InsufficientFundsException;
import io.github.jonarzz.kata.banking.account.statement.Table;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;
import io.github.jonarzz.kata.banking.account.validation.PositiveAmount;
import io.github.jonarzz.kata.banking.account.validation.ValidatedAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;

class CsvAccount<S> extends ValidatedAccount<S> {

    static Clock clock = Clock.systemDefaultZone();

    // may be implemented so that path is passed down from the factory
    private static final Path CSV_PATH = Path.of(System.getProperty("java.io.tmpdir"), "account.csv");

    private final StatementPrinter<S> statementPrinter;

    CsvAccount(StatementPrinter<S> statementPrinter) {
        this.statementPrinter = statementPrinter;
        try {
            // file handling could be different (e.g. marked by ID) - now it's counterintuitively not persistent
            Files.deleteIfExists(CSV_PATH);
            Files.createFile(CSV_PATH);
        } catch (IOException e) {
            // more sophisticated exceptional situations handling could be implemented
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void deposit(PositiveAmount positiveAmount) {
        appendToCsv(positiveAmount.value());
    }

    @Override
    protected void withdraw(PositiveAmount positiveAmount) throws InsufficientFundsException {
        BalancedDataRows balancedDataRows;
        synchronized (this) {
            balancedDataRows = calculateBalancedRows();
        }
        var balance = balancedDataRows.balance();
        var amount = positiveAmount.value();
        if (balance - amount < 0) {
            throw new InsufficientFundsException(balance);
        }
        appendToCsv(-amount);
    }

    @Override
    public S printStatement() {
        var dataRows = calculateBalancedRows().rows();
        var table = Table.withHeader(dataRows);
        return statementPrinter.print(table);
    }

    private void appendToCsv(int amount) {
        var csvRow = CsvRow.fromValues(LocalDate.now(clock), amount);
        var lineToWrite = csvRow + System.lineSeparator();
        try {
            Files.writeString(CSV_PATH, lineToWrite, APPEND);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private BalancedDataRows calculateBalancedRows() {
        try (var csvLines = Files.lines(CSV_PATH)) {
            return csvLines.map(CsvRow::fromLine)
                           .reduce(new BalancedDataRows(),
                                   (rows, csvRow) -> rows.append(csvRow.timestamp(), csvRow.amount()),
                                   BalancedDataRows::merge);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
