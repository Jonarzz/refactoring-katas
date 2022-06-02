package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparison.WithThreshold.expenses;
import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.GOLF;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.RESTAURANTS;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.TRAVEL;
import static io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicies.category;
import static io.github.jonarzz.kata.unusual.spending.payment.Timespan.from;
import static java.time.YearMonth.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.PaymentsAggregator;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ExpenseServiceTest {

    GroupingPolicy<Category> groupingPolicy = category();

    PaymentsAggregator paymentsAggregator = mock(PaymentsAggregator.class);
    ExpenseService expenseService = new ExpenseService(paymentsAggregator);

    @Test
    void noPaymentsInGivenTimespan() {
        var fromPreviousTimespan = from(of(2020, 4));
        var fromCurrentTimespan = from(of(2022, 5));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(eq(groupingPolicy), any()))
                .thenReturn(Map.of());

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(null));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void noPaymentsInPreviousTimespan_paymentsExistInCurrentTimespan() {
        var fromCurrentTimespan = from(of(2022, 5));
        var fromPreviousTimespan = from(of(2020, 4));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromPreviousTimespan))
                .thenReturn(Map.of());
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromCurrentTimespan))
                .thenReturn(Map.of(TRAVEL, usd(250, 0)));

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(null));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void paymentsExistInPreviousTimespan_noPaymentsInCurrentTimespan() {
        var fromPreviousTimespan = from(of(2020, 4));
        var fromCurrentTimespan = from(of(2022, 5));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromPreviousTimespan))
                .thenReturn(Map.of(TRAVEL, usd(250, 0)));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromCurrentTimespan))
                .thenReturn(Map.of());

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(null));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdNotMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var fromPreviousTimespan = from(of(2020, 4));
        var fromCurrentTimespan = from(of(2022, 5));
        var category = TRAVEL;
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(300, 0)));
        SpendingThreshold threshold = (base, compared) -> false;

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(threshold));

        assertThat(payments)
                .isEmpty();
    }

    @Test
    void thresholdMetForCategoryPresentInPreviousAndCurrentTimespan() {
        var fromPreviousTimespan = from(of(2020, 4));
        var fromCurrentTimespan = from(of(2022, 5));
        var category = TRAVEL;
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromPreviousTimespan))
                .thenReturn(Map.of(category, usd(250, 0)));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromCurrentTimespan))
                .thenReturn(Map.of(category, usd(500, 75)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(threshold));

        assertThat(payments)
                .extracting(CategorizedExpense::category, CategorizedExpense::amount)
                .containsExactly(
                        tuple("travel", "$500.75")
                );
    }

    @Test
    void categoriesPartiallyOverlappingBetweenPreviousAndCurrentTimespan_matchingCategoryExpensesExceedThreshold() {
        var fromCurrentTimespan = from(of(2022, 4));
        var fromPreviousTimespan = from(of(2020, 5));
        var matchingCategory = GOLF;
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromPreviousTimespan))
                .thenReturn(Map.of(TRAVEL, usd(1050, 99),
                                   matchingCategory, usd(250, 0)));
        when(paymentsAggregator.calculateTotalExpensesGroupedBy(groupingPolicy, fromCurrentTimespan))
                .thenReturn(Map.of(RESTAURANTS, usd(120, 50),
                                   matchingCategory, usd(505, 99)));
        SpendingThreshold threshold = (base, compared) -> true;

        var payments = expenseService.calculate(expenses(fromCurrentTimespan)
                                                        .groupedBy(category())
                                                        .comparedToExpenses(fromPreviousTimespan)
                                                        .matching(threshold));

        assertThat(payments)
                .extracting(CategorizedExpense::category, CategorizedExpense::amount)
                .containsExactly(
                        tuple("golf", "$505.99")
                );
    }
    
}