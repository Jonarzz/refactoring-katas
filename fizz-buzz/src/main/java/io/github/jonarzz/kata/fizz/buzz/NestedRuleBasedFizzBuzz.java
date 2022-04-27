package io.github.jonarzz.kata.fizz.buzz;

class NestedRuleBasedFizzBuzz implements FizzBuzz {

    @Override
    public String fizzbuzz(int number) {
        if (number % 3 == 0) {
            if (number % 5 == 0) {
                return "FizzBuzz";
            }
            return "Fizz";
        }
        if (number % 5 == 0) {
            return "Buzz";
        }
        return Integer.toString(number);
    }

}
