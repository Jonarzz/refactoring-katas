package io.github.jonarzz.kata.string.calculator.oop;

import io.github.jonarzz.kata.string.calculator.StringCalculator;

public class ObjectBasedStringCalculator implements StringCalculator {

    @Override
    public int add(String numbers) {
        var lines = numbers.split("\\n", -1);
        var delimiter = lines.length == 0
                        ? Delimiter.notCustomized()
                        : Delimiter.fromLine(lines[0]);
        var valuesToSplit = delimiter.isCustomized() ? lines[1] : numbers;
        return SeparatedValues.using(ToPositiveIntsSplitter.withMaxAcceptedValue(1000))
                              .on(delimiter)
                              .split(valuesToSplit)
                              .reduce(0, Integer::sum);
    }

}
