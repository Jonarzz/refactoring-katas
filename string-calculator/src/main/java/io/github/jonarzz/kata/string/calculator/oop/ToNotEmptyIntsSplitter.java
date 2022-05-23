package io.github.jonarzz.kata.string.calculator.oop;

import java.util.List;

class ToNotEmptyIntsSplitter extends ValuesValidatingSplitter<Integer> {

    ToNotEmptyIntsSplitter() {
        super(Integer::parseInt,
              List.of(new NotEmptyValueValidator()),
              List.of());
    }

}
