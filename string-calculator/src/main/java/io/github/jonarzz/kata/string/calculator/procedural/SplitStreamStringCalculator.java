package io.github.jonarzz.kata.string.calculator.procedural;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.util.regex.Pattern.quote;

import io.github.jonarzz.kata.string.calculator.StringCalculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SplitStreamStringCalculator implements StringCalculator {

    private static final String DELIMITER_PREFIX = "//";
    private static final Pattern EXTENDED_DELIMITER_PATTERN = Pattern.compile("\\[([^]]+)]");

    @Override
    public int add(String numbers) {
        var valueToSplit = numbers;
        var delimiterRegex = "[\\n,]";
        if (numbers.startsWith(DELIMITER_PREFIX)) {
            var split = numbers.split("\\n", -1);
            if (split.length == 2) {
                var unescapedDelimiter = split[0].replaceFirst("^" + DELIMITER_PREFIX, "");
                var matcher = EXTENDED_DELIMITER_PATTERN.matcher(unescapedDelimiter);
                if (matcher.find()) {
                    Set<String> delimiters = new HashSet<>();
                    do {
                        delimiters.add(quote(matcher.group(1)));
                    } while (matcher.find());
                    delimiterRegex = join("|", delimiters);
                } else {
                    delimiterRegex = Pattern.quote(unescapedDelimiter);
                }
                valueToSplit = split[1];
            }
        }
        return calculateSum(valueToSplit, delimiterRegex);
    }

    private int calculateSum(String valueToSplit, String delimiterRegex) {
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
