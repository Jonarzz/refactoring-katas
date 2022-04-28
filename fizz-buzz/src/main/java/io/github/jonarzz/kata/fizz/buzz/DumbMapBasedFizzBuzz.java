package io.github.jonarzz.kata.fizz.buzz;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * Use with great caution...
 */
class DumbMapBasedFizzBuzz implements FizzBuzz {

    private final Map<Integer, String> inputToOutput;

    DumbMapBasedFizzBuzz() {
        this(1, 100);
    }

    DumbMapBasedFizzBuzz(int fromInclusive, int toInclusive) {
        var notDumbFizzBuzz = new ModuloBasedFizzBuzz();
        inputToOutput = IntStream.rangeClosed(fromInclusive, toInclusive)
                                 .boxed()
                                 .collect(toMap(identity(),
                                                notDumbFizzBuzz::fizzbuzz));
    }

    @Override
    public String fizzbuzz(int number) {
        return inputToOutput.get(number);
    }

}
