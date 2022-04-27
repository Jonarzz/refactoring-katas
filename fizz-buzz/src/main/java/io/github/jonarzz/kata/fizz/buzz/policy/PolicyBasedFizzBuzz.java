package io.github.jonarzz.kata.fizz.buzz.policy;

import static java.util.Comparator.comparing;

import io.github.jonarzz.kata.fizz.buzz.FizzBuzz;

import java.util.List;
import java.util.stream.Stream;

public class PolicyBasedFizzBuzz implements FizzBuzz {

    // normally would be externally injected by Spring or otherwise
    private List<FizzBuzzingPolicy> policies = Stream.of(new FizzPolicy(),
                                                         new BuzzPolicy(),
                                                         new FizzBuzzPolicy(),
                                                         new FallbackNumberWrappingPolicy())
                                                     .sorted(comparing(FizzBuzzingPolicy::order))
                                                     .toList();

    @Override
    public String fizzbuzz(int number) {
        return policies.stream()
                       .filter(policy -> policy.isApplicableTo(number))
                       .findFirst()
                       .map(policy -> policy.mapValue(number))
                       .orElseThrow(() -> new AssertionError("Fallback policy not injected"));
    }

}
