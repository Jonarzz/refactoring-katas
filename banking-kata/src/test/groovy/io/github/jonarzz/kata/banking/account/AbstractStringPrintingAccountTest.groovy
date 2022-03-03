package io.github.jonarzz.kata.banking.account

import com.mercateo.test.clock.TestClock
import io.github.jonarzz.kata.banking.account.Account
import io.github.jonarzz.kata.banking.account.InsufficientFundsException
import spock.lang.Specification
import spock.lang.Timeout

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

import static java.time.ZoneId.systemDefault

abstract class AbstractStringPrintingAccountTest extends Specification {

    TestClock testClock
    Account<String> account

    def setup() {
        testClock = createTestClock()
        account = createAccount(testClock)
    }

    protected abstract Account<String> createAccount(Clock clock);

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

    def "Statement without any operation"() {
        when:
            def statement = account.printStatement()

        then: "print statement"
            statement == "Date  Amount  Balance"
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
            10  | 11    | 2020 | 1      || "Date        Amount  Balance\n$day.$month.$year      +1        1"
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
            10  | 11    | 2020 | 1      || "Date        Amount  Balance\n$day.$month.$year      +1        1\n$day.$month.$year      -1        0"
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
            Date       Amount  Balance
            1.1.2019     +100      100
            1.10.2019    -100        0
            15.2.2020    +200      200
            7.7.2021     -200        0
            10.9.2021    +300      300
            2.1.2022     -300        0\
            """.stripIndent()
    }

    def "Try to deposit a non-positive amount: #amount"() {
        when:
            account.deposit(amount)

        then: "exception is thrown"
            def exception = thrown IllegalArgumentException
            exception.message == "Deposition amount should be positive, but was " + amount

        where:
            amount << [0, -1, -100]
    }

    def "Try to withdraw a non-positive amount: #amount"() {
        when:
            account.withdraw(amount)

        then: "exception is thrown"
            def exception = thrown IllegalArgumentException
            exception.message == "Withdrawal amount should be positive, but was " + amount

        where:
            amount << [0, -1, -100]
    }

    def "Try to withdraw from an empty account"() {
        when:
            account.withdraw(100)

        then: "exception is thrown"
            def exception = thrown InsufficientFundsException
            exception.message == "Insufficient funds. Current balance: 0"
    }

    def "Try to withdraw more then deposited"() {
        given:
            def depositAmount = 100
            account.deposit(depositAmount)

        when:
            account.withdraw(depositAmount + 100)

        then: "exception is thrown"
            def exception = thrown InsufficientFundsException
            exception.message == "Insufficient funds. Current balance: " + depositAmount
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

    @Timeout(1)
    def "Multithreaded execution is supported - #invocationsCount invocations"() {
        given:
            testClock.set(createInstant(2022, 1, 30))

        when:
            executeTimesN(invocationsCount, account::deposit)
            executeTimesN(invocationsCount, account::withdraw)

        then:
            def statementLines = account.printStatement()
                                                   .lines()
                                                   .toList()
            statementLines.size() == 1 + 2 * invocationsCount // header + N deposits + N withdrawals
            // actual last amount withdrawn is unknown because of concurrency
            statementLines.last().matches("30\\.1\\.2022( {5}-\\d\\d| {6}-\\d) {8}0")

        where:
            invocationsCount << [10, 20, 50, 99]
    }

    @Timeout(2)
    def "Print statement during multithreaded operations execution - #invocationsCount invocations"() {
        given:
            testClock.set(createInstant(2022, 1, 30))
            def statementHolder = new AtomicReference()

        when:
            executeTimesN(invocationsCount, amount -> {
                account.deposit(amount)
                if (amount == invocationsCount / 4) {
                    statementHolder.set(account.printStatement())
                }
                account.withdraw(amount)
            })

        then:
            def finalStatementLinesCount = account.printStatement()
                    .lines()
                    .toList()
                    .size()
            finalStatementLinesCount == 1 + 2 * invocationsCount // header + N deposits + N withdrawals
            statementHolder.get()
                           .lines()
                           .toList()
                           .size() < finalStatementLinesCount

        where:
            invocationsCount << [100, 200, 500]
    }

    private static void executeTimesN(int nTimes, Consumer<Integer> executable) {
        def threadPool = Executors.newFixedThreadPool(Math.min(4, nTimes))
        def latch = new CountDownLatch(nTimes)
        for (def i = 1; i <= nTimes; i++) {
            def value = i
            threadPool.execute(() -> {
                executable.accept(value)
                latch.countDown()
            })
        }
        latch.await()
        threadPool.shutdownNow()
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