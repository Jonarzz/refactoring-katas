package io.github.jonarzz.kata.banking.account.memory;

import io.github.jonarzz.kata.banking.account.statement.printer.string.StringStatementPrinter;

public class StringStatementPrintingInMemoryAccountFactory extends AbstractInMemoryAccountFactory<String> {

    public StringStatementPrintingInMemoryAccountFactory() {
        super(new StringStatementPrinter());
    }

}
