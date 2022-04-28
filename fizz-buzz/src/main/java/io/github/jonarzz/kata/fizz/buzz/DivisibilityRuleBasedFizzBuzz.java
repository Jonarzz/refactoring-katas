package io.github.jonarzz.kata.fizz.buzz;

class DivisibilityRuleBasedFizzBuzz implements FizzBuzz {

    private static final int ASCII_ZERO = 48;

    @Override
    public String fizzbuzz(int number) {
        var numberAsString = Integer.toString(number);
        var numberChars = numberAsString.toCharArray();
        var lastCharIndex = numberChars.length - 1;
        var sumOfNumbers = 0;
        for (int i = 0; i < lastCharIndex; i++) {
            sumOfNumbers += toInt(numberChars[i]);
        }
        var lastDigit = toInt(numberChars[lastCharIndex]);
        var divisibleByThree = (sumOfNumbers + lastDigit) % 3 == 0;
        var divisibleByFive = lastDigit == 0 || lastDigit == 5;
        if (divisibleByThree && divisibleByFive) {
            return "FizzBuzz";
        }
        if (divisibleByThree) {
            return "Fizz";
        }
        if (divisibleByFive) {
            return "Buzz";
        }
        return numberAsString;
    }

    private int toInt(char numberChars) {
        return numberChars - ASCII_ZERO;
    }

}
