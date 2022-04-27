package io.github.jonarzz.kata.fizz.buzz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

abstract class BaseFizzBuzzTest {

    FizzBuzz fizzBuzz;

    BaseFizzBuzzTest(FizzBuzz fizzBuzz) {
        this.fizzBuzz = fizzBuzz;
    }

    @ParameterizedTest(name = "{argumentsWithNames}")
    @CsvFileSource(resources = "/100-test.csv")
    @DisplayName("From 1 to 100")
    final void fromOneToHundred(int input, String expectedOutput) {
        var output = fizzBuzz.fizzbuzz(input);

        assertEquals(expectedOutput, output);
    }

}