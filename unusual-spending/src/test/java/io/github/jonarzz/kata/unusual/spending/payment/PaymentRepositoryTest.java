package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan.fromWhole;
import static java.math.BigDecimal.valueOf;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.time.Month.JUNE;
import static java.time.Month.MAY;
import static java.time.YearMonth.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import io.github.jonarzz.kata.unusual.spending.test.LiquibaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LiquibaseExtension.class)
@SuppressWarnings("UnnecessaryLocalVariable")
class PaymentRepositoryTest {

    PaymentRepository repository = new PaymentRepository();

    @Test
    void getUserPaymentsBetween_firstUserInMay() {
        var userId = ONE;
        var timespan = fromWhole(of(2022, MAY));

        var results = repository.getUserPaymentsBetween(userId, timespan.start(), timespan.end());

        assertThat(results)
                .extracting("category.name", "cost.amount", "cost.currency")
                .containsExactly(
                        tuple("travel",    valueOf(190.99), USD),
                        tuple("groceries", valueOf(11.99),  USD),
                        tuple("golf",      valueOf(24.99),  USD),
                        tuple("groceries", valueOf(89.23),  USD),
                        tuple("travel",    valueOf(277.55), USD)
                );
    }

    @Test
    void getUserPaymentsBetween_secondUserInMay() {
        var userId = TWO;
        var timespan = fromWhole(of(2022, MAY));

        var results = repository.getUserPaymentsBetween(userId, timespan.start(), timespan.end());

        assertThat(results)
                .extracting("category.name", "cost.amount", "cost.currency")
                .containsExactly(
                        tuple("groceries", valueOf(22.71), USD)
                );
    }

    @Test
    void getUserPaymentsBetween_firstUserInJune() {
        var userId = ONE;
        var timespan = fromWhole(of(2022, JUNE));

        var results = repository.getUserPaymentsBetween(userId, timespan.start(), timespan.end());

        assertThat(results)
                .extracting("category.name", "cost.amount", "cost.currency")
                .containsExactly(
                        tuple("groceries",   valueOf(17.05),  USD),
                        tuple("travel",      valueOf(700.59), USD),
                        tuple("groceries",   valueOf(29.11),  USD),
                        tuple("travel",      valueOf(100.03), USD),
                        tuple("restaurants", valueOf(34.55),  USD),
                        tuple("restaurants", valueOf(10.00),  USD)
                );
    }

    @Test
    void getUserPaymentsBetween_secondUserInJune() {
        var userId = TWO;
        var timespan = fromWhole(of(2022, JUNE));

        var results = repository.getUserPaymentsBetween(userId, timespan.start(), timespan.end());

        assertThat(results)
                .extracting("category.name", "cost.amount", "cost.currency")
                .containsExactly(
                        tuple("travel", valueOf(133.99), USD)
                );
    }

}