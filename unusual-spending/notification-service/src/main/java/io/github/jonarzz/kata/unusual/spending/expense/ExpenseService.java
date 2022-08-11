package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

public class ExpenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseService.class);

    private final PaymentService paymentService;

    ExpenseService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Collection<UnusualExpense> calculateUnusualExpenses(TimestampedExpenseComparisonCriteria criteria) {
        LOGGER.debug("Calculating unusual expenses based on {}", criteria);
        var username = criteria.username();
        var policy = criteria.groupingPolicy();
        var baseExpenses = paymentService.aggregateTotalUserExpensesBy(policy, username, criteria.baseTimespan());
        var comparedExpenses = paymentService.aggregateTotalUserExpensesBy(policy, username, criteria.comparedTimespan());
        Collection<UnusualExpense> paymentsMatchingThreshold = new HashSet<>();
        for (var categoryToCurrentExpense : comparedExpenses.entrySet()) {
            var category = categoryToCurrentExpense.getKey();
            var current = categoryToCurrentExpense.getValue();
            var base = baseExpenses.get(category);
            if (ThresholdMatcherCreator.expense(current)
                                       .comparedTo(base)
                                       .satisfiesThreshold(criteria.threshold())) {
                var expense = new UnusualExpense(category, current);
                LOGGER.debug("Found {} for user {}", expense, username);
                paymentsMatchingThreshold.add(expense);
            }
        }
        return paymentsMatchingThreshold;
    }
}
