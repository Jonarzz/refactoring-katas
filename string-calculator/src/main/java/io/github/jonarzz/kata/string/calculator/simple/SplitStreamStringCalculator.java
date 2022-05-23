package io.github.jonarzz.kata.string.calculator.simple;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;

import io.github.jonarzz.kata.string.calculator.StringCalculator;

import java.util.ArrayList;
import java.util.List;
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
        var sum = 0;
        List<String> negativeValues = new ArrayList<>();
        for (var value : valueToSplit.split(delimiterRegex, -1)) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Separated values cannot be empty");
            }
            var intValue = parseInt(value);
            if (intValue < 0) {
                negativeValues.add(value);
            } else if (intValue <= 1000) {
                sum += intValue;
            }
        }
        if (!negativeValues.isEmpty()) {
            throw new IllegalArgumentException("Negatives not allowed, but got: " + join(", ", negativeValues));
        }
        return sum;
    }

}
