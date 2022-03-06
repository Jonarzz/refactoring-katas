package io.github.jonarzz.kata.banking.account.csv

import io.github.jonarzz.kata.banking.account.AbstractStringPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory
import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider

import java.time.Clock

import static io.github.jonarzz.kata.banking.account.factory.StatementType.STRING
import static io.github.jonarzz.kata.banking.account.factory.StorageType.CSV

class StringStatementPrintingCsvAccountTest extends AbstractStringPrintingAccountTest {

    private AccountFactory<String> accountFactory = AccountFactoryProvider.storedIn(CSV)
                                                                          .printing(STRING)

    @Override
    protected Account<String> createAccount(Clock clock) {
        CsvAccount.clock = clock
        return accountFactory.createAccount()
    }

}
