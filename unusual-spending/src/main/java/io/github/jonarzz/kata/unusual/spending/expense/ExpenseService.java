package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdMatcherCreator.expense;

import io.github.jonarzz.kata.unusual.spending.payment.PaymentService;

import java.util.Collection;
import java.util.HashSet;

public class ExpenseService {

    private final PaymentService paymentService;

    ExpenseService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // TODO web api

    public Collection<CategorizedExpense> calculate(TimestampedExpenseComparison expenseComparison) {
        var userId = expenseComparison.userId();
        var policy = expenseComparison.groupingPolicy();
        var baseExpenses = paymentService.aggregateTotalUserExpensesBy(policy, userId, expenseComparison.baseTimespan());
        var comparedExpenses = paymentService.aggregateTotalUserExpensesBy(policy, userId, expenseComparison.comparedTimespan());
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
