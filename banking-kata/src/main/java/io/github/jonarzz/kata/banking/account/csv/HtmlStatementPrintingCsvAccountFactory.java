package io.github.jonarzz.kata.banking.account.csv;

import io.github.jonarzz.kata.banking.account.statement.printer.html.HtmlStatementPrinter;

public class HtmlStatementPrintingCsvAccountFactory extends AbstractCsvAccountFactory<String> {

    public HtmlStatementPrintingCsvAccountFactory() {
        super(new HtmlStatementPrinter());
    }

}
