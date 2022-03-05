package io.github.jonarzz.kata.banking.account.statement.printer.string;

import java.util.ArrayList;
import java.util.List;

class Column {

    private final List<String> values = new ArrayList<>();

    private int maxValueLength;

    void addValue(String value) {
        values.add(value);
        maxValueLength = Math.max(value.length(), maxValueLength);
    }

    AlignedColumn alignLeft() {
        return AlignedColumn.left(values, maxValueLength);
    }

    AlignedColumn alignRight() {
        return AlignedColumn.right(values, maxValueLength);
    }

}
