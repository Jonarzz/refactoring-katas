package io.github.jonarzz.kata.banking.account.statement;

import io.github.jonarzz.kata.banking.account.statement.column.AlignedColumn;
import io.github.jonarzz.kata.banking.account.statement.column.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Statement {

    private static final String COLUMN_SEPARATOR = "  ";

    private List<DataRow> dataRows;
    private Function<String, Column> columnBuilderFactory;

    private Statement(List<DataRow> dataRows, Function<String, Column> columnBuilderFactory) {
        this.dataRows = dataRows;
        this.columnBuilderFactory = columnBuilderFactory;
    }

    public static Statement withHeader(List<DataRow> dataRows) {
        return new Statement(dataRows, Column::withHeader);
    }

    public static Statement withoutHeader(List<DataRow> dataRows) {
        return new Statement(dataRows, ignored -> Column.withoutHeader());
    }

    @Override
    public String toString() {
        var timestampColumn = columnBuilderFactory.apply("Date");
        var amountColumn = columnBuilderFactory.apply("Amount");
        var balanceColumn = columnBuilderFactory.apply("Balance");
        for (var row : dataRows) {
            timestampColumn.addValue(row.timestamp());
            amountColumn.addValue(row.amount());
            balanceColumn.addValue(row.balance());
        }
        return createStatement(timestampColumn, amountColumn, balanceColumn);
    }

    private String createStatement(Column firstColumn, Column... otherColumns) {
        var firstIterator = firstColumn.alignLeft()
                                       .valueIterator();
        var otherIterators = Arrays.stream(otherColumns)
                                   .map(Column::alignRight)
                                   .map(AlignedColumn::valueIterator)
                                   .toList();
        List<String> rows = new ArrayList<>();
        // factory methods are row-based, so each column has the same number of values
        while (firstIterator.hasNext()) {
            List<String> cells = new ArrayList<>();
            cells.add(firstIterator.next());
            otherIterators.stream()
                          .map(Iterator::next)
                          .forEach(cells::add);
            rows.add(String.join(COLUMN_SEPARATOR, cells));
        }
        return String.join("\n", rows);
    }

}
