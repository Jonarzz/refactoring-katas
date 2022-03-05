package io.github.jonarzz.kata.banking.account.statement;

import java.util.ArrayList;
import java.util.List;

public class TableFactory {

    public Table operationsTableWithHeader(List<Row> operationsRows) {
        List<Row> tableRows = new ArrayList<>();
        tableRows.add(new HeaderRow("Date", "Amount", "Balance"));
        tableRows.addAll(operationsRows);
        return new Table(tableRows);
    }

}
