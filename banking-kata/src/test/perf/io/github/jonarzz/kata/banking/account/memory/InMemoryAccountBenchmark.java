package io.github.jonarzz.kata.banking.account.memory;

public class InMemoryAccountBenchmark extends AccountBenchmarkRunner {

    public InMemoryAccountBenchmark() {
        super(new StringStatementPrintingInMemoryAccountFactory());
    }

}
