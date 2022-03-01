package io.github.jonarzz.kata.banking.account.statement.printer;

import io.github.jonarzz.kata.banking.account.statement.Table;

public interface StatementPrinter<S> {

    S print(Table table);

}
