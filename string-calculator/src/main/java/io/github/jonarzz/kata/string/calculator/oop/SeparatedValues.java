package io.github.jonarzz.kata.string.calculator.oop;

import java.util.Collection;
import java.util.function.BiFunction;

class SeparatedValues<T> {

    private Collection<T> values;

    private SeparatedValues(Collection<T> values) {
        this.values = values;
    }

    static <T> SeparatedValues.Creator<T> using(ValuesSplitter<T> splitter) {
        return new Creator<>(splitter);
    }

    @SuppressWarnings("SameParameterValue")
    <R> R reduce(R initialValue, BiFunction<R, T, R> accumulator) {
        return values.stream()
                     .reduce(initialValue, accumulator, (a, b) -> a);
    }

    static class Creator<T> {

        private ValuesSplitter<T> splitter;
        private Delimiter delimiter = Delimiter.notCustomized();

        private Creator(ValuesSplitter<T> splitter) {
            this.splitter = splitter;
        }

        Creator<T> on(Delimiter delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        SeparatedValues<T> split(String separatedValues) {
            return new SeparatedValues<>(splitter.split(separatedValues, delimiter));
        }

    }

}
