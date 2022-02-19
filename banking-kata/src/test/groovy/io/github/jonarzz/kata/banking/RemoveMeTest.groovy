package io.github.jonarzz.kata.banking

import spock.lang.Specification

class RemoveMeTest extends Specification { // TODO remove me

    def "Sum: #a + #b = #expectedResult"() {
        when: "add"
        def result = a + b

        then: "result is as expected"
        result == expectedResult

        where:
        a  | b | expectedResult
        1  | 2 | 3
        -5 | 2 | -3
    }

}