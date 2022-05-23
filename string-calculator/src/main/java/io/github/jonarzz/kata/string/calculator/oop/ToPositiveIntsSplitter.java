package io.github.jonarzz.kata.string.calculator.oop;

import java.util.List;

class ToPositiveIntsSplitter extends ValuesValidatingSplitter<Integer> {

    ToPositiveIntsSplitter() {
        super(Integer::parseInt,
              List.of(new NotEmptyValueValidator()),
              List.of(NotNegativeValueValidator::new));
    }

    private ToPositiveIntsSplitter(int maxValue) {
        super(Integer::parseInt,
              List.of(new NotEmptyValueValidator()),
              List.of(NotNegativeValueValidator::new,
                      () -> new NotGreaterThanValidator(maxValue)));
    }

    static ToPositiveIntsSplitter withMaxAcceptedValue(int maxValue) {
        return new ToPositiveIntsSplitter(maxValue);
    }

}
