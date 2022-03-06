package io.github.jonarzz.kata.banking.account;

import static io.github.jonarzz.kata.banking.account.factory.StatementType.STRING;
import static io.github.jonarzz.kata.banking.account.factory.StorageType.CSV;

import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider;

public class StringPrintingCsvAccountBenchmark extends AccountBenchmarkRunner {

    public StringPrintingCsvAccountBenchmark() {
        super(AccountFactoryProvider.storedIn(CSV)
                                    .printing(STRING));
    }

}
