package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy.category;
import static java.time.Month.MAY;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

class PaymentServiceTest {

    static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    final String payerUsername = "test_payer";

    final PaymentRepository repository = mock(PaymentRepository.class);
    final PaymentService service = new PaymentService(VALIDATOR_FACTORY.getValidator(), repository);

    @AfterAll
    static void afterAll() {
        VALIDATOR_FACTORY.close();
    }

    @Nested
    class CalculateTotalExpenseByCategory {

        AggregationTimespan aggregationTimespan = AggregationTimespan.of(YearMonth.of(2022, MAY));

        @Test
        void noPayments() {
            when(repository.getPaymentDetailsBetween(eq(payerUsername), any(LocalDate.class), any()))
                    .thenReturn(List.of());

            var result = service.aggregateTotalUserExpensesBy(category(), payerUsername, aggregationTimespan);

            assertThat(result)
                    .isEmpty();
        }

        @Test
        void singlePaymentInMultipleCategories() {
            Map<Category, Cost> priceByCategory = Map.of(
                    Category.named("RESTAURANTS"), usd(123, 99),
                    Category.named("GROCERIES"), usd(17, 33),
                    Category.named("TRAVEL"), usd(1999, 99)
            );
            when(repository.getPaymentDetailsBetween(eq(payerUsername), any(LocalDate.class), any()))
                    .thenReturn(priceByCategory.entrySet()
                                               .stream()
                                               .map(entry -> new PaymentDetails(randomUUID(),
                                                                                entry.getKey(),
                                                                                entry.getValue()))
                                               .toList());

            var result = service.aggregateTotalUserExpensesBy(category(), payerUsername, aggregationTimespan);

            assertThat(result)
                    .containsExactlyInAnyOrderEntriesOf(priceByCategory);
        }

        @Test
        void multiplePaymentsInSingleCategory() {
            var category = Category.named("GOLF");
            Supplier<IntStream> dollarValuesSupplier = () -> IntStream.of(15, 21, 90, 123);
            var dollarsSum = dollarValuesSupplier.get()
                                                 .sum();
            when(repository.getPaymentDetailsBetween(eq(payerUsername), any(LocalDate.class), any()))
                    .thenReturn(dollarValuesSupplier.get()
                                                    .mapToObj(dollars -> new PaymentDetails(randomUUID(),
                                                                                            category,
                                                                                            usd(dollars, 0)))
                                                    .toList());

            var result = service.aggregateTotalUserExpensesBy(category(), payerUsername, aggregationTimespan);

            assertThat(result)
                    .containsOnly(entry(category, usd(dollarsSum, 0)));
        }

        @Test
        void multiplePaymentsInMultipleCategories() {
            var categorySummingUpTo4 = Category.named("RESTAURANTS");
            var categorySummingUpTo2 = Category.named("GROCERIES");
            when(repository.getPaymentDetailsBetween(eq(payerUsername), any(LocalDate.class), any()))
                    .thenReturn(List.of(
                            new PaymentDetails(randomUUID(), categorySummingUpTo4, usd(1, 0)),
                            new PaymentDetails(randomUUID(), categorySummingUpTo2, usd(1, 0)),
                            new PaymentDetails(randomUUID(), categorySummingUpTo4, usd(1, 0)),
                            new PaymentDetails(randomUUID(), categorySummingUpTo4, usd(1, 0)),
                            new PaymentDetails(randomUUID(), categorySummingUpTo2, usd(1, 0)),
                            new PaymentDetails(randomUUID(), categorySummingUpTo4, usd(1, 0))
                    ));

            var result = service.aggregateTotalUserExpensesBy(category(), payerUsername, aggregationTimespan);

            assertThat(result)
                    .containsOnly(
                            entry(categorySummingUpTo2, usd(2, 0)),
                            entry(categorySummingUpTo4, usd(4, 0))
                    );
        }

    }

    @Nested
    class SaveTest {

        final UUID id = randomUUID();
        final OffsetDateTime timestamp = OffsetDateTime.now();

        @Test
        void emptyEvent() {
            var event = new PaymentRegisteredEvent(null, null);

            ThrowableAssert.ThrowingCallable methodUnderTest = () -> service.save(event);

            assertThatThrownBy(methodUnderTest)
                    .hasMessageStartingWith("Event validation failed. ")
                    .hasMessageContainingAll(
                            "Payer username cannot be blank. ",
                            "Payment details cannot be null. "
                    );
        }

        @Test
        void eventWithBlankPayerUsername() {
            var payerUsername = "   ";
            var details = new PaymentDetails(
                    id,
                    Category.named("groceries"),
                    Cost.usd(11.05)
            );
            var event = new PaymentRegisteredEvent(payerUsername, details);

            ThrowableAssert.ThrowingCallable methodUnderTest = () -> service.save(event);

            assertThatThrownBy(methodUnderTest)
                    .hasMessageStartingWith("Event validation failed. ")
                    .hasMessageContainingAll(
                            "Payer username cannot be blank. "
                    );
        }

        @Test
        void minimalValidEvent() {
            var id = this.id;
            var paymentDetails = new PaymentDetails(
                    id,
                    Category.named("travel"),
                    Cost.usd(12.35)
            );
            var event = new PaymentRegisteredEvent(payerUsername, paymentDetails);

            service.save(event);

            verify(repository)
                    .save(event);
        }

        @Test
        void eventWithAllDataFilled() {
            var id = this.id;
            var paymentDetails = new PaymentDetails(
                    id,
                    Category.named("travel"),
                    Cost.usd(12.35),
                    timestamp,
                    "Payment description"
            );
            var event = new PaymentRegisteredEvent(payerUsername, paymentDetails);

            service.save(event);

            verify(repository)
                    .save(event);
        }
    }

}