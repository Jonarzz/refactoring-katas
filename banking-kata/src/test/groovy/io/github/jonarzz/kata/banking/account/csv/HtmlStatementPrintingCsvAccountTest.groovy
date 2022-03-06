package io.github.jonarzz.kata.banking.account.csv

import io.github.jonarzz.kata.banking.account.AbstractHtmlPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory
import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider

import java.time.Clock

import static io.github.jonarzz.kata.banking.account.factory.StatementType.HTML
import static io.github.jonarzz.kata.banking.account.factory.StorageType.CSV

class HtmlStatementPrintingCsvAccountTest extends AbstractHtmlPrintingAccountTest {

    private AccountFactory<String> accountFactory = AccountFactoryProvider.storedIn(CSV)
                                                                          .printing(HTML)

    @Override
    protected Account<String> createAccount(Clock clock) {
        CsvAccount.clock = clock
        return accountFactory.createAccount()
    }

}
