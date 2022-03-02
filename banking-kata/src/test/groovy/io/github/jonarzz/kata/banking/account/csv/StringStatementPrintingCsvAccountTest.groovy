package io.github.jonarzz.kata.banking.account.csv

import io.github.jonarzz.kata.banking.account.AbstractStringPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory

import java.time.Clock

class StringStatementPrintingCsvAccountTest extends AbstractStringPrintingAccountTest {

    private AccountFactory<String> accountFactory = new StringStatementPrintingCsvAccountFactory()

    @Override
    protected Account<String> createAccount(Clock clock) {
        CsvAccount.clock = clock
        return accountFactory.createAccount()
    }

}
