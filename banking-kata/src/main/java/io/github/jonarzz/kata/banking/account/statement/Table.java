package io.github.jonarzz.kata.banking.account.statement;

import java.util.List;

public class Table {

    private List<Row> rows;

    Table(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return List.copyOf(rows);
    }

}
