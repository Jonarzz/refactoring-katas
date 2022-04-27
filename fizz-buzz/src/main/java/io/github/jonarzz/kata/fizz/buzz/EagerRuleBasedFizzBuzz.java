package io.github.jonarzz.kata.fizz.buzz;

class EagerRuleBasedFizzBuzz implements FizzBuzz {

    @Override
    public String fizzbuzz(int number) {
        var fizz = number % 3 == 0;
        var buzz = number % 5 == 0;
        if (fizz && buzz) {
            return "FizzBuzz";
        }
        if (fizz) {
            return "Fizz";
        }
        if (buzz) {
            return "Buzz";
        }
        return Integer.toString(number);
    }

}
