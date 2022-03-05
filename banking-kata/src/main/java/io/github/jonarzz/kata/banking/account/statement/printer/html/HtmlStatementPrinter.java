package io.github.jonarzz.kata.banking.account.statement.printer.html;

import io.github.jonarzz.kata.banking.account.statement.Table;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

public class HtmlStatementPrinter implements StatementPrinter<String> {

    @Override
    public String print(Table table) {
        var rows = table.getRows();
        if (rows.isEmpty()) {
            throw new IllegalStateException("Table should have at least 1 row");
        }
        var firstRow = rows.get(0);
        var firstDataRowIndex = 0;
        var htmlBuilder = new HtmlTableBuilder();
        if (firstRow.isHeader()) {
            firstDataRowIndex = 1;
            htmlBuilder.header(() -> {
                htmlBuilder.row(() -> {
                    for (var cell : firstRow.cells()) {
                        htmlBuilder.headerCell(cell);
                    }
                });
            });
        }
        if (rows.size() <= firstDataRowIndex) {
            return htmlBuilder.build();
        }
        var firstIndex = firstDataRowIndex;
        htmlBuilder.body(() -> {
            for (var rowIndex = firstIndex; rowIndex < rows.size(); rowIndex++) {
                var row = rows.get(rowIndex);
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
