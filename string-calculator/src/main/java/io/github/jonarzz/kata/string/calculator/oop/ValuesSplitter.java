package io.github.jonarzz.kata.string.calculator.oop;

import java.util.List;

interface ValuesSplitter<T> {

    List<T> split(String separatedValues, Delimiter delimiter);

}
