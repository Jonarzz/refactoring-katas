package io.github.jonarzz.kata.banking.account.memory

import io.github.jonarzz.kata.banking.account.AbstractStringPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory
import io.github.jonarzz.kata.banking.account.factory.AccountFactoryProvider

import java.time.Clock

import static io.github.jonarzz.kata.banking.account.factory.StatementType.STRING
import static io.github.jonarzz.kata.banking.account.factory.StorageType.MEMORY;

class StringStatementPrintingInMemoryAccountTest extends AbstractStringPrintingAccountTest {

    private AccountFactory<String> accountFactory = AccountFactoryProvider.storedIn(MEMORY)
                                                                          .printing(STRING)

    @Override
    protected Account<String> createAccount(Clock clock) {
        AccountOperation.clock = clock
        return accountFactory.createAccount()
    }

}
