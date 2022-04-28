package io.github.jonarzz.kata.fizz.buzz;

class RuleBasedFizzBuzzTest extends BaseFizzBuzzTest {

    RuleBasedFizzBuzzTest() {
        super(new ModuloBasedFizzBuzz());
    }

}