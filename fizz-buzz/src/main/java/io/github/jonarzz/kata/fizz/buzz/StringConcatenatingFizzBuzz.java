package io.github.jonarzz.kata.fizz.buzz;

class StringConcatenatingFizzBuzz implements FizzBuzz {

    @Override
    public String fizzbuzz(int number) {
        var output = "";
        if (number % 3 == 0) {
            output += "Fizz";
        }
        if (number % 5 == 0) {
            output += "Buzz";
        }
        if (output.isEmpty()) {
            output = Integer.toString(number);
        }
        return output;
    }

}
