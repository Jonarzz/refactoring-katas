package io.github.jonarzz.kata.banking.account;

import io.github.jonarzz.kata.banking.account.csv.StringStatementPrintingCsvAccountFactory;

public class StringPrintingCsvAccountBenchmark extends AccountBenchmarkRunner {

    public StringPrintingCsvAccountBenchmark() {
        super(new StringStatementPrintingCsvAccountFactory());
    }

}
