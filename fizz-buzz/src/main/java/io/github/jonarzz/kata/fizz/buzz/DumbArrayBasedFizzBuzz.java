package io.github.jonarzz.kata.fizz.buzz;

import java.util.stream.IntStream;

/**
 * Use with great caution...
 */
class DumbArrayBasedFizzBuzz implements FizzBuzz {

    private final String[] values;

    DumbArrayBasedFizzBuzz() {
        this(100);
    }

    DumbArrayBasedFizzBuzz(int toInclusive) {
        var notDumbFizzBuzz = new ModuloBasedFizzBuzz();
        values = IntStream.rangeClosed(0, toInclusive)
                          .mapToObj(notDumbFizzBuzz::fizzbuzz)
                          .toArray(String[]::new);
    }

    @Override
    public String fizzbuzz(int number) {
        return values[number];
    }

}
