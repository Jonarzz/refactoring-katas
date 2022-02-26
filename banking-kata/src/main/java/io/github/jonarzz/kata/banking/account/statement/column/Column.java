package io.github.jonarzz.kata.banking.account.statement.column;

import java.util.ArrayList;
import java.util.List;

public class Column {

    private final List<String> values = new ArrayList<>();

    private int maxValueLength;

    private Column() {

    }

    private Column(String header) {
        addValue(header);
    }

    public static Column withHeader(String header) {
        return new Column(header);
    }

    public static Column withoutHeader() {
        return new Column();
    }

    public void addValue(String value) {
        values.add(value);
        maxValueLength = Math.max(value.length(), maxValueLength);
    }

    public AlignedColumn alignLeft() {
        return AlignedColumn.left(values, maxValueLength);
    }

    public AlignedColumn alignRight() {
        return AlignedColumn.right(values, maxValueLength);
    }

}
