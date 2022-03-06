package io.github.jonarzz.kata.banking.account.statement.printer.html;

import io.github.jonarzz.kata.banking.account.statement.NonEmptyTable;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

import java.util.ArrayDeque;

public class HtmlStatementPrinter implements StatementPrinter<String> {

    @Override
    public String print(NonEmptyTable table) {
        var rows = new ArrayDeque<>(table.getRows());
        var firstRow = rows.removeFirst();
        var htmlBuilder = new HtmlTableBuilder();
        if (firstRow.isHeader()) {
            htmlBuilder.header(() -> {
                htmlBuilder.row(() -> {
                    for (var cell : firstRow.cells()) {
                        htmlBuilder.headerCell(cell);
                    }
                });
            });
        }
        if (rows.isEmpty()) {
            return htmlBuilder.build();
        }
        htmlBuilder.body(() -> {
            for (var row : rows) {
                htmlBuilder.row(() -> {
                    for (var cell : row.cells()) {
                        htmlBuilder.bodyCell(cell);
                    }
                });
            }
        });
        return htmlBuilder.build();
    }

}
