package io.github.jonarzz.kata.banking.account.statement.printer.string;

import io.github.jonarzz.kata.banking.account.statement.NonEmptyTable;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringStatementPrinter implements StatementPrinter<String> {

    private static final String COLUMN_SEPARATOR = "  ";

    @Override
    public String print(NonEmptyTable table) {
        var rows = new ArrayDeque<>(table.getRows());
        List<Column> columns = new ArrayList<>();
        for (var cell : rows.removeFirst().cells()) {
            var column = new Column();
            column.addValue(cell);
            columns.add(column);
        }
        for (var row : rows) {
            var rowCells = row.cells();
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
        while (firstIterator.hasNext()) {
            List<String> cells = new ArrayList<>();
            cells.add(firstIterator.next());
            otherIterators.stream()
                          .map(Iterator::next)
                          .forEach(cells::add);
            alignedRows.add(String.join(COLUMN_SEPARATOR, cells));
        }
        // using StringBuilder results in the same performance here, joining lists seems more natural
        return String.join("\n", alignedRows);
    }

}
