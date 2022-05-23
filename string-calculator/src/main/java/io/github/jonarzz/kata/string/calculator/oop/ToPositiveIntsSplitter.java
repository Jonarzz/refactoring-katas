package io.github.jonarzz.kata.string.calculator.oop;

import java.util.List;

class ToPositiveIntsSplitter extends ValuesValidatingSplitter<Integer> {

    ToPositiveIntsSplitter() {
        super(Integer::parseInt,
              List.of(new ThrowingValueValidator(new NotEmptyValueAcceptor())),
              List.of(new NotNegativeValueAcceptor()));
    }

}
