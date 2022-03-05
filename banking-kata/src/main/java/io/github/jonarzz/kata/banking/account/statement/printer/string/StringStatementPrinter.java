package io.github.jonarzz.kata.banking.account.statement.printer.string;

import io.github.jonarzz.kata.banking.account.statement.Table;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringStatementPrinter implements StatementPrinter<String> {

    private static final String COLUMN_SEPARATOR = "  ";

    @Override
    public String print(Table table) {
        var rows = table.getRows();
        if (rows.isEmpty()) {
            throw new IllegalStateException("Table should have at least 1 row");
        }
        List<Column> columns = new ArrayList<>();
        for (var cell : rows.get(0).cells()) {
            var column = new Column();
            column.addValue(cell);
            columns.add(column);
        }
        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            var rowCells = rows.get(rowIndex)
                               .cells();
            for (int cellIndex = 0; cellIndex < rowCells.length; cellIndex++) {
                columns.get(cellIndex)
                       .addValue(rowCells[cellIndex]);
            }
        }
        var columnsCount = columns.size();
        var firstIterator = columns.get(0)
                                   .alignLeft()
                                   .valueIterator();
        var otherIterators = columns.subList(1, columnsCount)
                                    .stream()
                                    .map(Column::alignRight)
                                    .map(AlignedColumn::valueIterator)
                                    .toList();
        List<String> alignedRows = new ArrayList<>();
        // factory methods are row-based, so each column has the same number of values
        while (firstIterator.hasNext()) {
            List<String> cells = new ArrayList<>();
            cells.add(firstIterator.next());
            otherIterators.stream()
                          .map(Iterator::next)
                          .forEach(cells::add);
            alignedRows.add(String.join(COLUMN_SEPARATOR, cells));
        }
        return String.join("\n", alignedRows);
    }

}
