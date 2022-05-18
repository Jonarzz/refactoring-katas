package io.github.jonarzz.kata.string.calculator.simple;

import io.github.jonarzz.kata.string.calculator.StringCalculator;

import java.util.Arrays;
import java.util.regex.Pattern;

class SplitStreamStringCalculator implements StringCalculator {

    private static final String DELIMITER_PREFIX = "//";

    @Override
    public int add(String numbers) {
        var valueToSplit = numbers;
        var delimiterRegex = "[\\n,]";
        if (numbers.startsWith(DELIMITER_PREFIX)) {
            var split = numbers.split("\\n", -1);
            if (split.length == 2) {
                var unescapedDelimiter = split[0].replaceFirst("^" + DELIMITER_PREFIX, "");
                delimiterRegex = Pattern.quote(unescapedDelimiter);
                valueToSplit = split[1];
            }
        }
        if (valueToSplit.isEmpty()) {
            return 0;
        }
        return Arrays.stream(valueToSplit.split(delimiterRegex, -1))
                     .peek(this::validate)
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

    private void validate(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Separated values cannot be empty");
        }
    }

}
