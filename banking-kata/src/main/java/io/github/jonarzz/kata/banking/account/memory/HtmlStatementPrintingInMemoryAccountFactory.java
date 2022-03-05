package io.github.jonarzz.kata.banking.account.memory;

import io.github.jonarzz.kata.banking.account.statement.printer.html.HtmlStatementPrinter;

public class HtmlStatementPrintingInMemoryAccountFactory extends AbstractInMemoryAccountFactory<String> {

    public HtmlStatementPrintingInMemoryAccountFactory() {
        super(new HtmlStatementPrinter());
    }

}
