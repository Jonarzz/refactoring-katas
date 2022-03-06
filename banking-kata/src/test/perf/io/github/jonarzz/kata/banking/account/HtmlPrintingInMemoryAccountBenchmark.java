package io.github.jonarzz.kata.banking.account;

import static io.github.jonarzz.kata.banking.account.factory.StatementType.HTML;
import static io.github.jonarzz.kata.banking.account.factory.StorageType.MEMORY;

import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider;

public class HtmlPrintingInMemoryAccountBenchmark extends AccountBenchmarkRunner {

    public HtmlPrintingInMemoryAccountBenchmark() {
        super(AccountFactoryProvider.storedIn(MEMORY)
                                    .printing(HTML));
    }

}
