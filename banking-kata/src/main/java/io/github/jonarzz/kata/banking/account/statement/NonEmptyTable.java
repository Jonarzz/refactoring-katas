package io.github.jonarzz.kata.banking.account.statement;

import java.util.ArrayList;
import java.util.List;

public class NonEmptyTable {

    private List<Row> rows;

    private NonEmptyTable(List<Row> rows) {
        if (rows.isEmpty()) {
            throw new IllegalStateException("Table should have at least 1 row");
        }
        this.rows = rows;
    }

    public static NonEmptyTable operationsTableWithHeader(List<Row> operationsRows) {
        List<Row> tableRows = new ArrayList<>();
        tableRows.add(new HeaderRow("Date", "Amount", "Balance"));
        tableRows.addAll(operationsRows);
        return new NonEmptyTable(tableRows);
    }

    public List<Row> getRows() {
        return List.copyOf(rows);
    }

}
