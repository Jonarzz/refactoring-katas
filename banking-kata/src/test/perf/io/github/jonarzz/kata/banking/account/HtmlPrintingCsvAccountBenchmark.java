package io.github.jonarzz.kata.banking.account;

import io.github.jonarzz.kata.banking.account.csv.HtmlStatementPrintingCsvAccountFactory;

public class HtmlPrintingCsvAccountBenchmark extends AccountBenchmarkRunner {

    public HtmlPrintingCsvAccountBenchmark() {
        super(new HtmlStatementPrintingCsvAccountFactory());
    }

}
