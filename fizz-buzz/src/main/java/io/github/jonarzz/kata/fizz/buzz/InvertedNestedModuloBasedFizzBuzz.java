package io.github.jonarzz.kata.fizz.buzz;

class InvertedNestedModuloBasedFizzBuzz implements FizzBuzz {

    @Override
    public String fizzbuzz(int number) {
        if (number % 5 == 0) {
            if (number % 3 == 0) {
                return "FizzBuzz";
            }
            return "Buzz";
        }
        if (number % 3 == 0) {
            return "Fizz";
        }
        return Integer.toString(number);
    }

}
