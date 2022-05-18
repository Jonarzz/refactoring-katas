package io.github.jonarzz.kata.string.calculator.oop

import io.github.jonarzz.kata.string.calculator.BaseStringCalculatorTest
import io.github.jonarzz.kata.string.calculator.StringCalculator

class ObjectBasedStringCalculatorTest extends BaseStringCalculatorTest {

    @Override
    StringCalculator createTestedCalculator() {
        return new ObjectBasedStringCalculator()
    }

}
