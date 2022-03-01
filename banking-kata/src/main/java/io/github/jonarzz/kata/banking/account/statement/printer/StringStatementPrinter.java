package io.github.jonarzz.kata.banking.account.statement.printer;

import io.github.jonarzz.kata.banking.account.statement.Table;
import io.github.jonarzz.kata.banking.account.statement.column.AlignedColumn;
import io.github.jonarzz.kata.banking.account.statement.column.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringStatementPrinter implements StatementPrinter<String> {

    private static final String COLUMN_SEPARATOR = "  ";

    @Override
    public String print(Table table) {
        var columns = table.getColumns();
        var columnsCount = columns.length;
        if (columnsCount == 0) {
            throw new IllegalStateException("Table should have at least 1 column");
        }
        var firstIterator = columns[0].alignLeft()
                                      .valueIterator();
        var otherIterators = Arrays.stream(Arrays.copyOfRange(columns, 1, columnsCount))
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
