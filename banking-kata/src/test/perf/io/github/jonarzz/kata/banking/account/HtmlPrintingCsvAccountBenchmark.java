package io.github.jonarzz.kata.banking.account;

import static io.github.jonarzz.kata.banking.account.factory.StatementType.HTML;
import static io.github.jonarzz.kata.banking.account.factory.StorageType.CSV;

import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider;

public class HtmlPrintingCsvAccountBenchmark extends AccountBenchmarkRunner {

    public HtmlPrintingCsvAccountBenchmark() {
        super(AccountFactoryProvider.storedIn(CSV)
                                    .printing(HTML));
    }

}
