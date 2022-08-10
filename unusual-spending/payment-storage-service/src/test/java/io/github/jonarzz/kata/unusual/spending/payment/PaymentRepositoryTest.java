package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static java.math.BigDecimal.valueOf;
import static java.time.Month.JUNE;
import static java.time.Month.MAY;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OFFSET_DATE_TIME;
import static org.assertj.core.groups.Tuple.tuple;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@QuarkusTest
@TestProfile(IsolatedTestProfile.class)
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
            var payerUsername = "test_user_1";
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

            var results = repository.getPaymentDetailsBetween(payerUsername, timespan.start(), timespan.end());

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
            var payerUsername = "test_user_2";
            var timespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

            var results = repository.getPaymentDetailsBetween(payerUsername, timespan.start(), timespan.end());

            assertThat(results)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple("groceries", valueOf(22.71), USD)
                    );
        }

        @Test
        void firstPayerInJune() {
            var payerUsername = "test_user_1";
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE));

            var results = repository.getPaymentDetailsBetween(payerUsername, timespan.start(), timespan.end());

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
            var payerUsername = "test_user_2";
            var timespan = AggregationTimespan.of(YearMonth.of(2022, JUNE));

            var results = repository.getPaymentDetailsBetween(payerUsername, timespan.start(), timespan.end());

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

        private String payerUsername = "saving_user";

        @Test
        void save_currencyAndCategoryAlreadyExistInDatabase_withoutProvidingTimestamp() {
            var categoryName = "golf";
            var amount = 33.99;
            var currency = USD;

            saveAndAssert(categoryName, amount, currency);
        }

        @Test
        void save_currencyAndCategoryAlreadyExistInDatabase_withProvidedTimestamp() {
            var categoryName = "golf";
            var amount = 33.99;
            var currency = USD;
            var timestamp = OffsetDateTime.of(2022, 7, 1, 11, 31, 43, 0, UTC);
            
            saveAndAssert(categoryName, amount, currency, timestamp);
        }

        @Test
        void save_categoryDoesNotExistInDatabase() {
            var categoryName = "subscriptions";
            var amount = 40.99;
            var currency = USD;

            saveAndAssert(categoryName, amount, currency);
        }

        @Test
        void save_currencyDoesNotExistInDatabase() {
            var categoryName = "travel";
            var amount = 2500.00;
            var currency = Currency.create("PLN", "pl-PL");

            saveAndAssert(categoryName, amount, currency);
        }

        @Test
        void save_tryToSaveSameEventTwice() {
            var categoryName = "golf";
            var amount = 12.34;
            var currency = USD;
            var details = new PaymentDetails(UUID.randomUUID(),
                                             Category.named(categoryName),
                                             Cost.create(amount, currency));
            var payment = new PaymentRegisteredEvent(payerUsername, details);
            var timeBefore = OffsetDateTime.now();

            assertThat(repository.save(payment))
                    .as("First save")
                    .isTrue();
            assertThat(repository.save(payment))
                    .as("Second save")
                    .isFalse();

            var saved = repository.getPaymentDetailsBetween(payerUsername, timeBefore, OffsetDateTime.now());
            assertThat(saved)
                    .singleElement()
                    .returns(categoryName, result -> result.category().getName())
                    .returns(amount, result -> result.cost().getAmount().doubleValue())
                    .returns(currency, result -> result.cost().getCurrency());
        }

        private void saveAndAssert(String categoryName, double amount, Currency currency) {
            saveAndAssert(categoryName, amount, currency, null);
        }

        private void saveAndAssert(String categoryName, double amount, Currency currency, OffsetDateTime time) {
            var details = new PaymentDetails(UUID.randomUUID(),
                                             Category.named(categoryName),
                                             Cost.create(amount, currency),
                                             time);
            var timeBefore = shiftedOrNow(time, toShift -> toShift.minusSeconds(1));
            var payment = new PaymentRegisteredEvent(payerUsername, details);

            repository.save(payment);

            var timeAfter = shiftedOrNow(time, toShift -> toShift.plusSeconds(1));
            var saved = repository.getPaymentDetailsBetween(payerUsername, timeBefore, timeAfter);
            assertThat(saved)
                    .extracting("category.name", "cost.amount", "cost.currency")
                    .containsExactly(
                            tuple(categoryName, valueOf(amount), currency)
                    );
            assertThat(saved)
                    .extracting(PaymentDetails::timestamp)
                    .singleElement(OFFSET_DATE_TIME)
                    .isBetween(timeBefore, timeAfter);
        }

        private OffsetDateTime shiftedOrNow(OffsetDateTime time, UnaryOperator<OffsetDateTime> shifter) {
            return Optional.ofNullable(time)
                           .map(shifter)
                           .orElseGet(OffsetDateTime::now);
        }

    }

}