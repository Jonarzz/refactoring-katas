package io.github.jonarzz.kata.banking.account

import com.mercateo.test.clock.TestClock
import groovy.transform.PackageScope
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.function.Consumer

import static java.time.ZoneId.systemDefault

@PackageScope
abstract class AbstractValidatedAccountTest extends Specification {

    TestClock testClock
    Account<String> account

    def setup() {
        testClock = createTestClock()
        account = createAccount(testClock)
    }

    protected abstract Account<String> createAccount(Clock clock);

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
            def amount = 100
            account.withdraw(amount)

        then: "exception is thrown"
            def exception = thrown InsufficientFundsException
            exception.message == "Insufficient funds. Current balance: 0, requested amount: " + amount
    }

    def "Try to withdraw more then deposited"() {
        given:
            def depositAmount = 100
            def withdrawalAmount = depositAmount + 100
            account.deposit(depositAmount)

        when:
            account.withdraw(withdrawalAmount)

        then: "exception is thrown"
            def exception = thrown InsufficientFundsException
            exception.message == "Insufficient funds. Current balance: " + depositAmount + ", requested amount: " + withdrawalAmount
    }

    @PackageScope
    static void executeTimesN(int nTimes, Consumer<Integer> executable) {
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

    @PackageScope
    static TestClock createTestClock() {
        return TestClock.fixed(Instant.now(), systemDefault());
    }


    @PackageScope
    static Instant createInstant(int year, int month, int day) {
        return LocalDate.of(year, month, day)
                .atStartOfDay(systemDefault())
                .toInstant()
    }

}
