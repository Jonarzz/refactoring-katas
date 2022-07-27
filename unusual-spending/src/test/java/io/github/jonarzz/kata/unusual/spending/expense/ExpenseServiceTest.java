package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.MultiplicationThreshold.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparisonCriteria.forUserId;
import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy.category;
import static java.time.Month.APRIL;
import static java.time.Month.MAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.github.jonarzz.kata.unusual.spending.payment.PaymentService;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.Map;

class ExpenseServiceTest {

    static final long PAYER_ID = 2;

    AggregationPolicy<Category> aggregationPolicy = category();
    AggregationTimespan fromCurrentTimespan = AggregationTimespan.of(YearMonth.of(2022, APRIL));
    AggregationTimespan fromPreviousTimespan = AggregationTimespan.of(YearMonth.of(2020, MAY));

    PaymentService paymentService = mock(PaymentService.class);
    ExpenseService expenseService = new ExpenseService(paymentService);

    @Test
    void noPaymentsInGivenTimespan() {
        when(paymentService.aggregateTotalUserExpensesBy(eq(aggregationPolicy), eq(PAYER_ID), any()))
                .thenReturn(Map.of());

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void noPaymentsInPreviousTimespan_paymentsExistInCurrentTimespan() {
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromPreviousTimespan))
                .thenReturn(Map.of());
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(250, 0)));

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void paymentsExistInPreviousTimespan_noPaymentsInCurrentTimespan() {
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromCurrentTimespan))
                .thenReturn(Map.of());

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdNotMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var category = Category.named("TRAVEL");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(300, 0)));
        SpendingThreshold threshold = (base, compared) -> false;

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(1000)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var category = Category.named("TRAVEL");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(500, 75)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(10)));

        assertThat(payments)
                .extracting(UnusualExpense::category, UnusualExpense::amount)
                .containsExactly(
                        tuple("travel", "$500.75")
                );
    }

    @Test
    void categoriesPartiallyOverlappingBetweenPreviousAndCurrentTimespan_matchingCategoryExpensesExceedThreshold() {
        var matchingCategory = Category.named("GOLF");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(1050, 99),
                                   matchingCategory, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, PAYER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(Category.named("RESTAURANTS"), usd(120, 50),
                                   matchingCategory, usd(505, 99)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculateUnusualExpenses(forUserId(PAYER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(0)));

        assertThat(payments)
                .extracting(UnusualExpense::category, UnusualExpense::amount)
                .containsExactly(
                        tuple("golf", "$505.99")
                );
    }

}