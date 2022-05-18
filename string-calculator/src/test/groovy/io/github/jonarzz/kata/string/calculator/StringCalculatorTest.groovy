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

    def "Various number of values (more than 2)"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input                       || expectedResult
            "1,2,3"                     || 6
            "-1,-2,-3"                  || -6
            "-1,1,-2,-3,2,3"            || 0
            "0,0,0,0,0"                 || 0
            "1,1,1,1,1,1,1,1,1,1,1,1,1" || 13
            "1,2,3,4,5,6,7"             || 28
            "1,-1,1,-1,1"               || 1
    }

    def "Various number of values with various allowed separators"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input                         || expectedResult
            "1\n2,3"                      || 6
            "1,2\n3"                      || 6
            "0\n3"                        || 3
            "10\n0\n5"                    || 15
            "-1\n-2,-3"                   || -6
            "-1\n1,-2\n-3,2\n3"           || 0
            "0,0,0,0,0"                   || 0
            "1,1,1,1,1,1,1,1\n1,1,1,1\n1" || 13
            "1\n2\n3,4,5,6\n7"            || 28
            "1\n-1,1\n-1,1"               || 1
    }

    def "Empty separated values are not allowed"() {
        when:
            calculator.add(input)

        then:
            def exception = thrown IllegalArgumentException
            exception.message == "Separated values cannot be empty"

        where:
            input << ["1,\n", "1\n,", "1,\n,\n", ",", "\n", ",\n1", "1,2,3\n,", "1,2,3,\n", "1,2\n,3\n,4"]
    }

}
