package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdMatcherCreator.expense;

import io.github.jonarzz.kata.unusual.spending.payment.PaymentsAggregator;

import java.util.Collection;
import java.util.HashSet;

public class ExpenseService {

    private final PaymentsAggregator paymentsAggregator;

    ExpenseService(PaymentsAggregator paymentsAggregator) {
        this.paymentsAggregator = paymentsAggregator;
    }

    public Collection<CategorizedExpense> calculate(TimestampedExpenseComparison.WithThreshold expenseComparison) {
        var policy = expenseComparison.groupingPolicy();
        var baseExpenses = paymentsAggregator.calculateTotalExpensesGroupedBy(policy, expenseComparison.baseTimespan());
        var comparedExpenses = paymentsAggregator.calculateTotalExpensesGroupedBy(policy, expenseComparison.comparedTimespan());
        Collection<CategorizedExpense> paymentsMatchingThreshold = new HashSet<>();
        for (var categoryToCurrentExpense : comparedExpenses.entrySet()) {
            var category = categoryToCurrentExpense.getKey();
            var current = categoryToCurrentExpense.getValue();
            var base = baseExpenses.get(category);
            if (expense(current)
                    .comparedTo(base)
                    .satisfiesThreshold(expenseComparison.threshold())) {
                paymentsMatchingThreshold.add(new CategorizedExpense(category, current));
            }
        }
        return paymentsMatchingThreshold;
    }
}
