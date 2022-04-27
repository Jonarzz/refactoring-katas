package io.github.jonarzz.kata.fizz.buzz;

import io.github.jonarzz.kata.fizz.buzz.policy.PolicyBasedFizzBuzz;

class PolicyBasedFizzBuzzTest extends BaseFizzBuzzTest {

    PolicyBasedFizzBuzzTest() {
        super(new PolicyBasedFizzBuzz());
    }

}
