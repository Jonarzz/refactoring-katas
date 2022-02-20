package io.github.jonarzz.kata.banking

import com.mercateo.test.clock.TestClock
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.InsufficientFundsException
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.LocalDate

import static java.time.ZoneId.systemDefault

abstract class AbstractAccountTest extends Specification {

    private TestClock testClock
    private Account account

    def setup() {
        testClock = createTestClock()
        account = createAccount(testClock)
    }

    protected abstract Account createAccount(Clock clock);

    def "Kata acceptance test"() {
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

    def "Singular deposition of #amount"() {
        given:
            testClock.set(createInstant(year, month, day))

        when: "deposit"
            account.deposit(amount)

        then: "print statement"
            account.printStatement() == expectedStatement

        where:
            day | month | year | amount || expectedStatement
            10  | 11    | 2020 | 0      || "Date        Amount  Balance\n$day.$month.$year       0        0"
            11  | 10    | 1994 | 100    || "Date        Amount  Balance\n$day.$month.$year    +100      100"
            22  | 12    | 2001 | 5000   || "Date        Amount  Balance\n$day.$month.$year   +5000     5000"
            31  | 10    | 2022 | 10_000 || "Date        Amount  Balance\n$day.$month.$year  +10000    10000"
    }

    def "Singular deposition and withdrawal of #amount"() {
        when: "deposit"
            testClock.set(createInstant(year, month, day))
            account.deposit(amount)
        and: "withdraw"
            testClock.set(createInstant(year, month, day))
            account.withdraw(amount)

        then: "print statement"
            account.printStatement() == expectedStatement

        where:
            day | month | year | amount || expectedStatement
            10  | 11    | 2020 | 0      || "Date        Amount  Balance\n$day.$month.$year       0        0\n$day.$month.$year       0        0"
            11  | 10    | 1994 | 100    || "Date        Amount  Balance\n$day.$month.$year    +100      100\n$day.$month.$year    -100        0"
            22  | 12    | 2001 | 5000   || "Date        Amount  Balance\n$day.$month.$year   +5000     5000\n$day.$month.$year   -5000        0"
            31  | 10    | 2022 | 10_000 || "Date        Amount  Balance\n$day.$month.$year  +10000    10000\n$day.$month.$year  -10000        0"
    }

    def "Singular deposition and withdrawal of a large amount"() {
        when: "deposit"
            testClock.set(createInstant(2020, 10, 11))
            account.deposit(200_000_000)
        and: "withdraw"
            testClock.set(createInstant(2020, 11, 20))
            account.withdraw(150_000_000)

        then: "print statement"
            account.printStatement() == """\
            Date            Amount    Balance
            11.10.2020  +200000000  200000000
            20.11.2020  -150000000   50000000\
            """.stripIndent()
    }

    def "Multiple depositions"() {
        given:
            testClock.set(createInstant(2020, 12, 15))

        when: "deposit multiple times"
            account.deposit(100)
            account.deposit(500)
            account.deposit(2000)
            account.deposit(300)
            account.deposit(18000)

        then: "print statement"
            account.printStatement() == """\
            Date        Amount  Balance
            15.12.2020    +100      100
            15.12.2020    +500      600
            15.12.2020   +2000     2600
            15.12.2020    +300     2900
            15.12.2020  +18000    20900\
            """.stripIndent()
    }

    def "Multiple depositions and withdrawals"() {
        given:
            testClock.set(createInstant(2020, 12, 21))

        when: "deposit and withdraw multiple times"
            account.deposit(500)
            account.withdraw(20)
            account.deposit(600)
            account.withdraw(900)
            account.deposit(100)
            account.withdraw(250)

        then: "print statement"
            account.printStatement() == """\
            Date        Amount  Balance
            21.12.2020    +500      500
            21.12.2020     -20      480
            21.12.2020    +600     1080
            21.12.2020    -900      180
            21.12.2020    +100      280
            21.12.2020    -250       30\
            """.stripIndent()
    }

    def "Operations performed on various dates"() {
        when: "deposit and withdraw multiple times at various dates"
            testClock.set(createInstant(2019, 1, 1))
            account.deposit(100)
            testClock.set(createInstant(2019, 10, 1))
            account.withdraw(100)
            testClock.set(createInstant(2020, 2, 15))
            account.deposit(200)
            testClock.set(createInstant(2021, 7, 7))
            account.withdraw(200)
            testClock.set(createInstant(2021, 9, 10))
            account.deposit(300)
            testClock.set(createInstant(2022, 1, 2))
            account.withdraw(300)

        then: "print statement"
            account.printStatement() == """\
            Date        Amount  Balance
            1.1.2019      +100      100
            1.10.2019     -100        0
            15.2.2020     +200      200
            7.7.2021      -200        0
            10.9.2021     +300      300
            2.1.2022      -300        0\
            """.stripIndent()
    }

    def "Try to deposit a negative amount"() {
        when:
            account.deposit(-100)

        then: "exception is thrown"
            def exception = thrown IllegalArgumentException
            exception.message == "Deposition amount cannot be negative"
    }

    def "Try to withdraw a negative amount"() {
        when:
            account.withdraw(-100)

        then: "exception is thrown"
            def exception = thrown IllegalArgumentException
            exception.message == "Withdrawal amount cannot be negative"
    }

    def "Try to withdraw from an empty account"() {
        when:
            account.withdraw(100)

        then: "exception is thrown"
            thrown InsufficientFundsException
    }

    def "Try to withdraw more then deposited"() {
        given:
            def depositAmount = 100
            account.deposit(depositAmount)

        when:
            account.withdraw(depositAmount + 100)

        then: "exception is thrown"
            thrown InsufficientFundsException
    }

    def "Try to withdraw more then deposited and then withdraw all funds"() {
        given:
            testClock.set(createInstant(2020, 10, 15))
            def depositAmount = 100
            account.deposit(depositAmount)

        when: "try to withdraw more than deposited"
            account.withdraw(depositAmount + 100)

        then: "exception is thrown"
            thrown InsufficientFundsException

        when: "withdraw all funds"
            account.withdraw(depositAmount)

        then: "print statement"
            account.printStatement() == """\
            Date        Amount  Balance
            15.10.2020    +100      100
            15.10.2020    -100        0\
            """.stripIndent()
    }

    private static TestClock createTestClock() {
        return TestClock.fixed(Instant.now(), systemDefault());
    }

    private static Instant createInstant(int year, int month, int day) {
        return LocalDate.of(year, month, day)
                .atStartOfDay(systemDefault())
                .toInstant()
    }


}