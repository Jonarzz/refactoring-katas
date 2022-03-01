package io.github.jonarzz.kata.banking.account.memory

import io.github.jonarzz.kata.banking.account.AbstractStringPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory

import java.time.Clock;

class StringStatementPrintingInMemoryAccountTest extends AbstractStringPrintingAccountTest {

    private AccountFactory<String> accountFactory = new StringStatementPrintingInMemoryAccountFactory()

    @Override
    protected Account<String> createAccount(Clock clock) {
        AccountOperation.clock = clock
        return accountFactory.createAccount()
    }

}
