package io.github.jonarzz.kata.banking.account.statement.column;

import java.util.ArrayList;
import java.util.List;

public class Column {

    private List<String> values = new ArrayList<>();

    private Column() {

    }

    private Column(String header) {
        values.add(header);
    }

    public static Column withHeader(String header) {
        return new Column(header);
    }

    public static Column withoutHeader() {
        return new Column();
    }

    public void addValue(String value) {
        values.add(value);
    }

    public AlignedColumn alignLeft() {
        return AlignedColumn.left(values);
    }

    public AlignedColumn alignRight() {
        return AlignedColumn.right(values);
    }

}
