package io.github.jonarzz.kata.fizz.buzz;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Use with great caution...
 */
class DumbListBasedFizzBuzz implements FizzBuzz {

    private final List<String> values;

    DumbListBasedFizzBuzz() {
        this(100);
    }

    DumbListBasedFizzBuzz(int toInclusive) {
        var notDumbFizzBuzz = new ModuloBasedFizzBuzz();
        values = IntStream.rangeClosed(0, toInclusive)
                          .mapToObj(notDumbFizzBuzz::fizzbuzz)
                          .toList();
    }

    @Override
    public String fizzbuzz(int number) {
        return values.get(number);
    }

}
