package io.github.jonarzz.kata.banking.account;

import static io.github.jonarzz.kata.banking.account.factory.StatementType.STRING;
import static io.github.jonarzz.kata.banking.account.factory.StorageType.MEMORY;

import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider;

public class StringPrintingInMemoryAccountBenchmark extends AccountBenchmarkRunner {

    public StringPrintingInMemoryAccountBenchmark() {
        super(AccountFactoryProvider.storedIn(MEMORY)
                                    .printing(STRING));
    }

}
