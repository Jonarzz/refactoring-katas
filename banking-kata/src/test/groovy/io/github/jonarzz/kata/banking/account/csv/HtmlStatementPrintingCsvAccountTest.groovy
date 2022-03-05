package io.github.jonarzz.kata.banking.account.csv

import io.github.jonarzz.kata.banking.account.AbstractHtmlPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory

import java.time.Clock

class HtmlStatementPrintingCsvAccountTest extends AbstractHtmlPrintingAccountTest {

    private AccountFactory<String> accountFactory = new HtmlStatementPrintingCsvAccountFactory()

    @Override
    protected Account<String> createAccount(Clock clock) {
        CsvAccount.clock = clock
        return accountFactory.createAccount()
    }

}
