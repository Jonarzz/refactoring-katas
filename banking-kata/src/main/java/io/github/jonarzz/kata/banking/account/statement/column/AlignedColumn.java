package io.github.jonarzz.kata.banking.account.statement.column;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class AlignedColumn {

    private List<String> alignedValues;

    private AlignedColumn(List<String> alignedValues) {
        this.alignedValues = alignedValues;
    }

    public Iterator<String> valueIterator() {
        return alignedValues.iterator();
    }

    static AlignedColumn left(List<String> values, int maxValueLength) {
        return align(values, maxValueLength, AlignedColumn::alignedLeft);
    }

    static AlignedColumn right(List<String> values, int maxValueLength) {
        return align(values, maxValueLength, AlignedColumn::alignedRight);
    }

    private static AlignedColumn align(List<String> values, int maxValueLength,
                                       BiFunction<String, Integer, String> aligner) {
        return new AlignedColumn(values.stream()
                                       .map(value -> aligner.apply(value, maxValueLength))
                                       .toList());
    }

    private static String alignedLeft(String value, int maxWidth) {
        return value + nSpaces(maxWidth - value.length());
    }

    private static String alignedRight(String value, int maxWidth) {
        return nSpaces(maxWidth - value.length()) + value;
    }

    private static String nSpaces(int n) {
        return " ".repeat(n);
    }

}
