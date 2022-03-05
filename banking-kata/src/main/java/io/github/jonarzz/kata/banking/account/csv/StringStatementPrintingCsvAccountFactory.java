package io.github.jonarzz.kata.banking.account.csv;

import io.github.jonarzz.kata.banking.account.statement.printer.string.StringStatementPrinter;

public class StringStatementPrintingCsvAccountFactory extends AbstractCsvAccountFactory<String> {

    public StringStatementPrintingCsvAccountFactory() {
        super(new StringStatementPrinter());
    }

}
