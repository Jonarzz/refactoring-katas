package io.github.jonarzz.kata.fizz.buzz.policy;

interface FizzBuzzingPolicy {

    boolean isApplicableTo(int number);

    String mapValue(int number);

    // similar to e.g. @Order annotation is Spring
    default int order() {
        return Integer.MAX_VALUE;
    }

}
