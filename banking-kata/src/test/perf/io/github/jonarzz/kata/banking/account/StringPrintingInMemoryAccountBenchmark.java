package io.github.jonarzz.kata.banking.account;

import io.github.jonarzz.kata.banking.account.memory.StringStatementPrintingInMemoryAccountFactory;

public class StringPrintingInMemoryAccountBenchmark extends AccountBenchmarkRunner {

    public StringPrintingInMemoryAccountBenchmark() {
        super(new StringStatementPrintingInMemoryAccountFactory());
    }

}
