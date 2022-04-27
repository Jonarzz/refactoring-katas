package io.github.jonarzz.kata.fizz.buzz.policy;

class FizzPolicy implements FizzBuzzingPolicy {

    @Override
    public boolean isApplicableTo(int number) {
        return number % 3 == 0;
    }

    @Override
    public String mapValue(int number) {
        return "Fizz";
    }

    @Override
    public int order() {
        return 1;
    }

}
