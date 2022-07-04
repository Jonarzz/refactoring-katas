package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static java.math.BigDecimal.valueOf;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.time.Month.JUNE;
import static java.time.Month.MAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.test.LiquibaseExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnnecessaryLocalVariable")
class PaymentRepositoryTest {

    PaymentRepository repository = new PaymentRepository(LiquibaseExtension.URL,
                                                         LiquibaseExtension.USERNAME,
                                                         Optional.of(LiquibaseExtension.PASSWORD));

    @Nested
    @ExtendWith(LiquibaseExtension.class)
    class GetUserPaymentsBetween {

        @Test
        void firstUserInMay() {
            var userId = ONE;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

            var results = repository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("travel", valueOf(190.99), USD),
                            tuple("groceries", valueOf(11.99), USD),
                            tuple("golf", valueOf(24.99), USD),
                            tuple("groceries", valueOf(89.23), USD),
                            tuple("travel", valueOf(277.55), USD)
                    );
        }

        @Test
        void secondUserInMay() {
            var userId = TWO;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

            var results = repository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("groceries", valueOf(22.71), USD)
                    );
        }

        @Test
        void firstUserInJune() {
            var userId = ONE;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE));

            var results = repository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("groceries", valueOf(17.05), USD),
                            tuple("travel", valueOf(700.59), USD),
                            tuple("groceries", valueOf(29.11), USD),
                            tuple("travel", valueOf(100.03), USD),
                            tuple("restaurants", valueOf(34.55), USD),
                            tuple("restaurants", valueOf(10.00), USD)
                    );
        }

        @Test
        void secondUserInJune() {
            var userId = TWO;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE));

            var results = repository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("travel", valueOf(133.99), USD)
                    );
        }

    }

    @Nested
    @ExtendWith(LiquibaseExtension.class)
    class SavePayment {

        // in case of any errors GetUserPaymentsBetween tests should be fixed first

        @Test
        void tryToSave_userWithGivenIdDoesNotExist() {
            var payment = new PaymentRegisteredEvent(UUID.randomUUID(), BigInteger.TEN, null, OffsetDateTime.now());

            assertThatThrownBy(() -> repository.save(payment))
                    .hasMessage("Payer with ID 10 does not exist");
        }

        @Test
        void save_currencyAndCategoryAlreadyExistInDatabase() {
            var payerId = ONE;
            var categoryName = "golf";
            var amount = 33.99;
            var currency = USD;

            saveAndAssert(payerId, categoryName, amount, currency);
        }

        @Test
        void save_categoryDoesNotExistInDatabase() {
            var payerId = TWO;
            var categoryName = "subscriptions";
            var amount = 40.99;
            var currency = USD;

            saveAndAssert(payerId, categoryName, amount, currency);
        }

        @Test
        void save_currencyDoesNotExistInDatabase() {
            var payerId = ONE;
            var categoryName = "travel";
            var amount = 2500.00;
            var currency = Currency.create("PLN", "pl-PL");

            saveAndAssert(payerId, categoryName, amount, currency);
        }

        private void saveAndAssert(BigInteger payerId, String categoryName, double amount, Currency currency) {
            var details = new PaymentDetails(Category.named(categoryName),
                                             Cost.create(amount, currency));
            var timeBefore = OffsetDateTime.now();
            var payment = new PaymentRegisteredEvent(UUID.randomUUID(), payerId, details, OffsetDateTime.now());

            repository.save(payment);

            var saved = repository.getPaymentDetailsBetween(payerId, timeBefore, OffsetDateTime.now());
            assertThat(saved)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple(categoryName, valueOf(amount), currency)
                    );
        }

    }

    @Nested
    class DatabaseExceptionHandling {

        PaymentRepository invalidRepository = new PaymentRepository(
                "invalid url",
                "invalid user",
                Optional.of("invalid password")
        );

        @Test
        void exceptionThrownWhenNoSuitableDriverExistsForGivenUrl() {
            var payerId = ONE;
            var timestamp = OffsetDateTime.now();

            assertThatThrownBy(() -> invalidRepository.getPaymentDetailsBetween(payerId, timestamp, timestamp))
                    .hasCauseInstanceOf(SQLException.class);
        }

    }

}