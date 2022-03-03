package io.github.jonarzz.kata.banking.account.memory;

import io.github.jonarzz.kata.banking.account.csv.StringStatementPrintingCsvAccountFactory;

public class CsvAccountBenchmark extends AccountBenchmarkRunner {

    public CsvAccountBenchmark() {
        super(new StringStatementPrintingCsvAccountFactory());
    }

}
