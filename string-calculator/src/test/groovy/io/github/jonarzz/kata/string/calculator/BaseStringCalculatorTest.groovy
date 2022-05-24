package io.github.jonarzz.kata.string.calculator


import spock.lang.Shared
import spock.lang.Specification

abstract class BaseStringCalculatorTest extends Specification {

    @Shared
    StringCalculator calculator

    def setupSpec() {
        calculator = createTestedCalculator();
    }

    abstract StringCalculator createTestedCalculator()

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
            "0,0,0,0,0"                 || 0
            "1,1,1,1,1,1,1,1,1,1,1,1,1" || 13
            "1,2,3,4,5,6,7"             || 28
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
            "0,0,0,0,0"                   || 0
            "1,1,1,1,1,1,1,1\n1,1,1,1\n1" || 13
            "1\n2\n3,4,5,6\n7"            || 28
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

    def "Negative values are not allowed"() {
        when:
            calculator.add(input)

        then:
            def exception = thrown IllegalArgumentException
            exception.message == "Negatives not allowed, but got: " + negativeValues

        where:
            input           || negativeValues
            "1,-1"          || "-1"
            "-1,1"          || "-1"
            "-1,-1"         || "-1, -1"
            "1,2,3,-2,5,-1" || "-2, -1"
            "1,1,1,1,-1,1"  || "-1"
            "-1,-2,-3,-4"   || "-1, -2, -3, -4"
    }

    def "Various number of values with delimiter defined by the input"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input                || expectedResult
            "//;\n1;2"           || 3
            "//,\n1,2,3"         || 6
            "//;\n1;2;3"         || 6
            "//;\n"              || 0
            "//^\n0^0^2"         || 2
            "//.\n3.2.1"         || 6
            "//[***]\n1***2***3" || 6
            "//[^.,]\n3^.,1^.,2" || 6
            "//[_x]\n3_x1_x2"    || 6
    }

    def "Ignore values bigger than 1000"() {
        when:
            def result = calculator.add(input)

        then:
            expectedResult == result

        where:
            input            || expectedResult
            "2,1001"         || 2
            "1000,1"         || 1001
            "1,1000"         || 1001
            "1,1000,2"       || 1003
            "1001,1002"      || 0
            "0,1,81249124,0" || 1
    }

}
