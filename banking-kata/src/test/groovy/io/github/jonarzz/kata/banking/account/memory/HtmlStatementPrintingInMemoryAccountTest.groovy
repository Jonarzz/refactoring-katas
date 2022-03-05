package io.github.jonarzz.kata.banking.account.memory

import io.github.jonarzz.kata.banking.account.AbstractHtmlPrintingAccountTest
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.AccountFactory

import java.time.Clock

class HtmlStatementPrintingInMemoryAccountTest extends AbstractHtmlPrintingAccountTest {

    private AccountFactory<String> accountFactory = new HtmlStatementPrintingInMemoryAccountFactory()

    @Override
    protected Account<String> createAccount(Clock clock) {
        AccountOperation.clock = clock
        return accountFactory.createAccount()
    }

}
