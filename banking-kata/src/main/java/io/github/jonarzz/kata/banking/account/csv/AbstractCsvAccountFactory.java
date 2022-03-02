package io.github.jonarzz.kata.banking.account.csv;

import io.github.jonarzz.kata.banking.account.Account;
import io.github.jonarzz.kata.banking.account.AccountFactory;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

abstract class AbstractCsvAccountFactory<S> implements AccountFactory<S> {

    private StatementPrinter<S> statementPrinter;

    AbstractCsvAccountFactory(StatementPrinter<S> statementPrinter) {
        this.statementPrinter = statementPrinter;
    }

    @Override
    public final Account<S> createAccount() {
        return new CsvAccount<>(statementPrinter);
    }

}
