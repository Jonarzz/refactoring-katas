package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static java.math.BigDecimal.valueOf;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.time.Month.JUNE;
import static java.time.Month.MAY;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@QuarkusTest
@TestProfile(PaymentRepositoryTest.ConfigProfile.class)
@SuppressWarnings("UnnecessaryLocalVariable")
class PaymentRepositoryTest {

    @Inject
    PaymentRepository repository;

    @Nested
    class GetPayerPaymentsBetween {

        private final ZoneOffset defaultInsertOffset = OffsetDateTime.now()
                                                                     .getOffset();

        @Test
        void firstPayerInMay() {
            var payerId = ONE;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY), defaultInsertOffset);

            var results = repository.getPaymentDetailsBetween(payerId, timespan.start(), timespan.end());

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
        void secondPayerInMay() {
            var payerId = TWO;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

            var results = repository.getPaymentDetailsBetween(payerId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("groceries", valueOf(22.71), USD)
                    );
        }

        @Test
        void firstPayerInJune() {
            var payerId = ONE;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE), defaultInsertOffset);

            var results = repository.getPaymentDetailsBetween(payerId, timespan.start(), timespan.end());

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
        void secondPayerInJune() {
            var payerId = TWO;
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE));

            var results = repository.getPaymentDetailsBetween(payerId, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("travel", valueOf(133.99), USD)
                    );
        }

    }

    @Nested
    class SavePayment {

        // in case of any errors GetPayerPaymentsBetween tests should be fixed first

        @Test
        void tryToSave_payerWithGivenIdDoesNotExist() {
            var payment = new PaymentRegisteredEvent(UUID.randomUUID(), BigInteger.TEN, null, OffsetDateTime.now());

            assertThatThrownBy(() -> repository.save(payment))
                    .hasMessage("Payer with ID 10 does not exist");
        }

        @Test
        void save_currencyAndCategoryAlreadyExistInDatabase_withoutProvidingTimestamp() {
            var payerId = ONE;
            var categoryName = "golf";
            var amount = 33.99;
            var currency = USD;

            saveAndAssert(payerId, categoryName, amount, currency);
        }

        @Test
        void save_currencyAndCategoryAlreadyExistInDatabase_withProvidedTimestamp() {
            var payerId = ONE;
            var categoryName = "golf";
            var amount = 33.99;
            var currency = USD;
            var timestamp = OffsetDateTime.of(2022, 7, 1, 11, 31, 43, 0, UTC);
            
            saveAndAssert(payerId, categoryName, amount, currency, timestamp);
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

        @Test
        void save_tryToSaveSameEventTwice() {
            var payerId = ONE;
            var categoryName = "golf";
            var details = new PaymentDetails(Category.named(categoryName),
                                             Cost.create(12.34, USD));
            var payment = new PaymentRegisteredEvent(UUID.randomUUID(), payerId, details, null);
            var timeBefore = OffsetDateTime.now();

            assertThat(repository.save(payment))
                    .as("First save")
                    .isTrue();
            assertThat(repository.save(payment))
                    .as("Second save")
                    .isFalse();

            var saved = repository.getPaymentDetailsBetween(payerId, timeBefore, OffsetDateTime.now());
            assertThat(saved)
                    .hasSize(1);
        }

        private void saveAndAssert(BigInteger payerId, String categoryName, double amount, Currency currency) {
            saveAndAssert(payerId, categoryName, amount, currency, null);
        }

        private void saveAndAssert(BigInteger payerId, String categoryName, double amount, Currency currency, OffsetDateTime time) {
            var details = new PaymentDetails(Category.named(categoryName),
                                             Cost.create(amount, currency));
            var timeBefore = shiftedOrNow(time, toShift -> toShift.minusSeconds(1));
            var payment = new PaymentRegisteredEvent(UUID.randomUUID(), payerId, details, time);

            repository.save(payment);

            var timeAfter = shiftedOrNow(time, toShift -> toShift.plusSeconds(1));
            var saved = repository.getPaymentDetailsBetween(payerId, timeBefore, timeAfter);
            assertThat(saved)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple(categoryName, valueOf(amount), currency)
                    );
        }

        private OffsetDateTime shiftedOrNow(OffsetDateTime time, UnaryOperator<OffsetDateTime> shifter) {
            return Optional.ofNullable(time)
                           .map(shifter)
                           .orElseGet(OffsetDateTime::now);
        }

    }

    public static class ConfigProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    // disable Docker testcontainers
                    "quarkus.devservices.enabled", "false",
                    // disable PaymentRegistrationListener startup
                    "quarkus.arc.test.disable-application-lifecycle-observers", "true",
                    "quarkus.liquibase.change-log", "test-changeLog.yaml",
                    "quarkus.datasource.jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                    "quarkus.datasource.username", "user",
                    "quarkus.datasource.password", "password",
                    // required JMS-related properties
                    "jms.payment.destination", "dummy",
                    "jms.payment.client-id", "dummy",
                    "quarkus.artemis.url", "dummy"
            );
        }
    }
}