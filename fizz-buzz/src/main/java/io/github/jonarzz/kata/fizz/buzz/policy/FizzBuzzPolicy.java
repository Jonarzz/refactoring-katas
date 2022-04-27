package io.github.jonarzz.kata.fizz.buzz.policy;

class FizzBuzzPolicy implements FizzBuzzingPolicy {

    @Override
    public boolean isApplicableTo(int number) {
        return number % 3 == 0 && number % 5 == 0;
    }

    @Override
    public String mapValue(int number) {
        return "FizzBuzz";
    }

    @Override
    public int order() {
        return 0;
    }

}
