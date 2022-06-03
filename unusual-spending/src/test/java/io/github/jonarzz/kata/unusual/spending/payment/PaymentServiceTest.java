package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy.category;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan.fromWhole;
import static java.time.YearMonth.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

class PaymentServiceTest {

    PaymentRepository repository = mock(PaymentRepository.class);
    PaymentService aggregator = new PaymentService(repository);

    @Nested
    class CalculateTotalExpenseByCategory {

        @Test
        void noPayments() {
            when(repository.getPaymentsBetween(any(), any()))
                    .thenReturn(List.of());

            var result = aggregator.aggregateTotalExpensesBy(category(), fromWhole(of(2022, 5)));

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
            when(repository.getPaymentsBetween(any(), any()))
                    .thenReturn(priceByCategory.entrySet()
                                               .stream()
                                               .map(entry -> new Payment(entry.getKey(), entry.getValue()))
                                               .toList());

            var result = aggregator.aggregateTotalExpensesBy(category(), fromWhole(of(2022, 5)));

            assertThat(result)
                    .containsExactlyInAnyOrderEntriesOf(priceByCategory);
        }

        @Test
        void multiplePaymentsInSingleCategory() {
            var category = Category.named("GOLF");
            Supplier<IntStream> dollarValuesSupplier = () -> IntStream.of(15, 21, 90, 123);
            var dollarsSum = dollarValuesSupplier.get()
                                                 .sum();
            when(repository.getPaymentsBetween(any(), any()))
                    .thenReturn(dollarValuesSupplier.get()
                                                    .mapToObj(dollars -> new Payment(category, usd(dollars, 0)))
                                                    .toList());

            var result = aggregator.aggregateTotalExpensesBy(category(), fromWhole(of(2022, 5)));

            assertThat(result)
                    .containsOnly(entry(category, usd(dollarsSum, 0)));
        }

        @Test
        void multiplePaymentsInMultipleCategories() {
            var categorySummingUpTo4 = Category.named("RESTAURANTS");
            var categorySummingUpTo2 = Category.named("GROCERIES");
            when(repository.getPaymentsBetween(any(), any()))
                    .thenReturn(List.of(
                            new Payment(categorySummingUpTo4, usd(1, 0)),
                            new Payment(categorySummingUpTo2, usd(1, 0)),
                            new Payment(categorySummingUpTo4, usd(1, 0)),
                            new Payment(categorySummingUpTo4, usd(1, 0)),
                            new Payment(categorySummingUpTo2, usd(1, 0)),
                            new Payment(categorySummingUpTo4, usd(1, 0))
                    ));

            var result = aggregator.aggregateTotalExpensesBy(category(), fromWhole(of(2022, 5)));

            assertThat(result)
                    .containsOnly(
                            entry(categorySummingUpTo2, usd(2, 0)),
                            entry(categorySummingUpTo4, usd(4, 0))
                    );
        }

    }

}