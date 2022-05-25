package io.github.jonarzz.kata.string.calculator.procedural

import io.github.jonarzz.kata.string.calculator.BaseStringCalculatorTest
import io.github.jonarzz.kata.string.calculator.StringCalculator

class SplitStreamStringCalculatorTest extends BaseStringCalculatorTest {

    @Override
    StringCalculator createTestedCalculator() {
        return new SplitStreamStringCalculator()
    }

}
