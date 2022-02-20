package io.github.jonarzz.kata.banking

import com.mercateo.test.clock.TestClock
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime

import static java.time.ZoneId.systemDefault

abstract class AbstractAccountTest extends Specification {

    protected abstract Account createAccount(Clock clock);

    def "Kata acceptance test"() {
        given:
            def testClock = createTestClock();
            def account = createAccount(testClock);

        when: "deposit"
            testClock.set(createInstant(2015, 12, 24))
            account.deposit(500)
        and: "withdraw"
            testClock.set(createInstant(2016, 8, 23))
            account.withdraw(100)

        then: "print statement"
            account.printStatement() == """\
            Date        Amount  Balance
            24.12.2015    +500      500
            23.8.2016     -100      400\
            """.stripIndent()
    }

    private static TestClock createTestClock() {
        return TestClock.fixed(OffsetDateTime.now());
    }

    private static Instant createInstant(int year, int month, int day) {
        return LocalDate.of(year, month, day)
                        .atStartOfDay(systemDefault())
                        .toInstant()
    }


}