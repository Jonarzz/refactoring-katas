package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.List;

class ToNotEmptyIntsSplitter implements ValuesSplitter<Integer> {

    @Override
    public List<Integer> split(String separatedValues, Delimiter delimiter) {
        if (separatedValues.isEmpty()) {
            return List.of();
        }
        List<Integer> list = new ArrayList<>();
        for (var value : delimiter.split(separatedValues)) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Separated values cannot be empty");
            }
            list.add(parseInt(value));
        }
        return list;
    }

}
