package io.github.jonarzz.kata.banking.account.statement.column;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class AlignedColumn {

    private List<String> alignedValues;

    private AlignedColumn(List<String> alignedValues) {
        this.alignedValues = alignedValues;
    }

    public Iterator<String> valueIterator() {
        return alignedValues.iterator();
    }

    static AlignedColumn left(List<String> values) {
        return align(values, AlignedColumn::alignedLeft);
    }

    static AlignedColumn right(List<String> values) {
        return align(values, AlignedColumn::alignedRight);
    }

    private static AlignedColumn align(List<String> values, BiFunction<String, Integer, String> aligner) {
        var maxValueLength = maxLength(values);
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

    private static int maxLength(Collection<String> values) {
        return values.stream()
                     .map(String::length)
                     .max(naturalOrder())
                     .orElse(0);
    }

    private static String nSpaces(int n) {
        return IntStream.range(0, n)
                        .mapToObj(i -> " ")
                        .collect(joining());
    }

}
