package io.github.jonarzz.kata.fizz.buzz.policy;

class FallbackNumberWrappingPolicy implements FizzBuzzingPolicy {

    @Override
    public boolean isApplicableTo(int number) {
        return true;
    }

    @Override
    public String mapValue(int number) {
        return Integer.toString(number);
    }

}
