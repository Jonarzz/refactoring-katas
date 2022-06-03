package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdValue.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparison.forUserId;
import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy.category;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan.fromWhole;
import static java.math.BigInteger.TWO;
import static java.time.YearMonth.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.github.jonarzz.kata.unusual.spending.payment.PaymentService;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;

class ExpenseServiceTest {

    static final BigInteger USER_ID = TWO;

    AggregationPolicy<Category> aggregationPolicy = category();

    PaymentService paymentService = mock(PaymentService.class);
    ExpenseService expenseService = new ExpenseService(paymentService);

    @Test
    void noPaymentsInGivenTimespan() {
        var fromPreviousTimespan = fromWhole(of(2020, 4));
        var fromCurrentTimespan = fromWhole(of(2022, 5));
        when(paymentService.aggregateTotalUserExpensesBy(eq(aggregationPolicy), eq(USER_ID), any()))
                .thenReturn(Map.of());

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void noPaymentsInPreviousTimespan_paymentsExistInCurrentTimespan() {
        var fromCurrentTimespan = fromWhole(of(2022, 5));
        var fromPreviousTimespan = fromWhole(of(2020, 4));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromPreviousTimespan))
                .thenReturn(Map.of());
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(250, 0)));

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void paymentsExistInPreviousTimespan_noPaymentsInCurrentTimespan() {
        var fromPreviousTimespan = fromWhole(of(2020, 4));
        var fromCurrentTimespan = fromWhole(of(2022, 5));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromCurrentTimespan))
                .thenReturn(Map.of());

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(50)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdNotMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var fromPreviousTimespan = fromWhole(of(2020, 4));
        var fromCurrentTimespan = fromWhole(of(2022, 5));
        var category = Category.named("TRAVEL");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(300, 0)));
        SpendingThreshold threshold = (base, compared) -> false;

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(1000)));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var fromPreviousTimespan = fromWhole(of(2020, 4));
        var fromCurrentTimespan = fromWhole(of(2022, 5));
        var category = Category.named("TRAVEL");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(500, 75)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(10)));

        assertThat(payments)
                .extracting(CategorizedExpense::category, CategorizedExpense::amount)
                .containsExactly(
                        tuple("travel", "$500.75")
                );
    }

    @Test
    void categoriesPartiallyOverlappingBetweenPreviousAndCurrentTimespan_matchingCategoryExpensesExceedThreshold() {
        var fromCurrentTimespan = fromWhole(of(2022, 4));
        var fromPreviousTimespan = fromWhole(of(2020, 5));
        var matchingCategory = Category.named("GOLF");
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromPreviousTimespan))
                .thenReturn(Map.of(Category.named("TRAVEL"), usd(1050, 99),
                                   matchingCategory, usd(250, 0)));
        when(paymentService.aggregateTotalUserExpensesBy(aggregationPolicy, USER_ID, fromCurrentTimespan))
                .thenReturn(Map.of(Category.named("RESTAURANTS"), usd(120, 50),
                                   matchingCategory, usd(505, 99)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculate(forUserId(USER_ID)
                                                        .aggregateExpenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToAggregatedExpenses(fromPreviousTimespan)
                                                        .increasedByAtLeast(percentage(0)));

        assertThat(payments)
                .extracting(CategorizedExpense::category, CategorizedExpense::amount)
                .containsExactly(
                        tuple("golf", "$505.99")
                );
    }
    
}