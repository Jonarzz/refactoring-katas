package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.PaymentsAggregator;

import java.util.Collection;
import java.util.TreeSet;

class ExpenseService {

    private final PaymentsAggregator paymentsAggregator;

    ExpenseService(PaymentsAggregator paymentsAggregator) {
        this.paymentsAggregator = paymentsAggregator;
    }

    Collection<CategorizedExpense> calculate(TimestampedExpenseComparison.WithThreshold expenseComparison) {
        var policy = expenseComparison.groupingPolicy();
        var baseExpenses = paymentsAggregator.calculateTotalExpensesGroupedBy(policy, expenseComparison.baseTimespan());
        var comparedExpenses = paymentsAggregator.calculateTotalExpensesGroupedBy(policy, expenseComparison.comparedTimespan());
        Collection<CategorizedExpense> paymentsMatchingThreshold = new TreeSet<>();
        for (var categoryToCurrentExpense : comparedExpenses.entrySet()) {
            var category = categoryToCurrentExpense.getKey();
            var currentExpense = categoryToCurrentExpense.getValue();
            var baseExpense = baseExpenses.get(category);
            if (baseExpense != null && currentExpense.comparedTo(baseExpense)
                                                     .satisfiesThreshold(expenseComparison.threshold())) {
                paymentsMatchingThreshold.add(new CategorizedExpense(category, currentExpense));
            }
        }
        return paymentsMatchingThreshold;
    }
}
