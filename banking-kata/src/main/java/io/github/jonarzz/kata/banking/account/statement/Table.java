package io.github.jonarzz.kata.banking.account.statement;

import io.github.jonarzz.kata.banking.account.statement.column.Column;

import java.util.List;
import java.util.function.Function;

public class Table {

    private Column[] columns;

    private Table(Column... columns) {
        this.columns = columns;
    }

    public static Table withHeader(List<DataRow> dataRows) {
        return create(dataRows, Column::withHeader);
    }

    public static Table withoutHeader(List<DataRow> dataRows) {
        return create(dataRows, ignored -> Column.withoutHeader());
    }

    public Column[] getColumns() {
        return columns;
    }

    private static Table create(List<DataRow> dataRows, Function<String, Column> columnCreator) {
        var timestampColumn = columnCreator.apply("Date");
        var amountColumn = columnCreator.apply("Amount");
        var balanceColumn = columnCreator.apply("Balance");
        for (var row : dataRows) {
            timestampColumn.addValue(row.timestamp());
            amountColumn.addValue(row.amount());
            balanceColumn.addValue(row.balance());
        }
        return new Table(timestampColumn, amountColumn, balanceColumn);
    }

}
