package io.github.jonarzz.kata.banking.account;

import io.github.jonarzz.kata.banking.account.memory.HtmlStatementPrintingInMemoryAccountFactory;

public class HtmlPrintingInMemoryAccountBenchmark extends AccountBenchmarkRunner {

    public HtmlPrintingInMemoryAccountBenchmark() {
        super(new HtmlStatementPrintingInMemoryAccountFactory());
    }

}
