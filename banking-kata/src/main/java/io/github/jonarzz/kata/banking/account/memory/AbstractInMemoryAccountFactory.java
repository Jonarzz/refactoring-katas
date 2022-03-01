package io.github.jonarzz.kata.banking.account.memory;

import io.github.jonarzz.kata.banking.account.Account;
import io.github.jonarzz.kata.banking.account.AccountFactory;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

abstract class AbstractInMemoryAccountFactory<S> implements AccountFactory<S> {

    private StatementPrinter<S> statementPrinter;

    AbstractInMemoryAccountFactory(StatementPrinter<S> statementPrinter) {
        this.statementPrinter = statementPrinter;
    }

    @Override
    public final Account<S> createAccount() {
        return new InMemoryAccount<>(statementPrinter);
    }

}
