package io.github.jonarzz.kata.banking.account.statement.printer;

import io.github.jonarzz.kata.banking.account.statement.NonEmptyTable;

public interface StatementPrinter<S> {

    S print(NonEmptyTable table);

}
