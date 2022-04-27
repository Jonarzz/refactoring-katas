package io.github.jonarzz.kata.fizz.buzz.policy;

class BuzzPolicy implements FizzBuzzingPolicy {

    @Override
    public boolean isApplicableTo(int number) {
        return number % 5 == 0;
    }

    @Override
    public String mapValue(int number) {
        return "Buzz";
    }

    @Override
    public int order() {
        return 1;
    }

}
