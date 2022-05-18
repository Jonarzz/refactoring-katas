package io.github.jonarzz.kata.string.calculator

import spock.lang.Shared
import spock.lang.Specification

class StringCalculatorTest extends Specification {

    @Shared
    def calculator = new SplitStreamStringCalculator()

    def "Empty string"() {
        when:
            def result = calculator.add("")

        then:
            0 == result
    }

    def "Single value"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input || expectedResult
            "-10" || -10
            "-1"  || -1
            "0"   || 0
            "1"   || 1
            "10"  || 10
    }

    def "Pair of values"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input     || expectedResult
            "-10,10"  || 0
            "-10,5"   || -5
            "-10,0"   || -10
            "0,-10"   || -10
            "0,0"     || 0
            "10,0"    || 10
            "0,10"    || 10
            "11,17"   || 28
            "789,698" || 1487
    }

}
