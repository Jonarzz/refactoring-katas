package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

public class TimestampedExpenseComparisonCriteria implements
        TimestampedExpenseComparisonCriteriaApi.WithId,
        TimestampedExpenseComparisonCriteriaApi.WithComparedTimespan,
        TimestampedExpenseComparisonCriteriaApi.WithGrouping,
        TimestampedExpenseComparisonCriteriaApi.WithBaseTimespan {

    private String username;
    private AggregationTimespan comparedTimespan;
    private AggregationPolicy<Category> aggregationPolicy;
    private AggregationTimespan baseTimespan;
    private SpendingThreshold threshold;

    private TimestampedExpenseComparisonCriteria(String username) {
        this.username = username;
    }

    public static TimestampedExpenseComparisonCriteriaApi.WithId forUsername(String username) {
        return new TimestampedExpenseComparisonCriteria(username);
    }

    @Override
    public TimestampedExpenseComparisonCriteriaApi.WithComparedTimespan aggregateExpenses(AggregationTimespan comparedTimespan) {
        this.comparedTimespan = comparedTimespan;
        return this;
    }

    @Override
    public TimestampedExpenseComparisonCriteriaApi.WithGrouping groupedBy(AggregationPolicy<Category> aggregationPolicy) {
        this.aggregationPolicy = aggregationPolicy;
        return this;
    }

    @Override
    public TimestampedExpenseComparisonCriteriaApi.WithBaseTimespan comparedToAggregatedExpenses(AggregationTimespan baseTimespan) {
        this.baseTimespan = baseTimespan;
        return this;
    }

    @Override
    public TimestampedExpenseComparisonCriteria increasedByAtLeast(MultiplicationThreshold multiplicationThreshold) {
        threshold = new ExpensesIncreasedByAtLeast(multiplicationThreshold);
        return this;
    }

    @Override
    public String toString() {
        return "TimestampedExpenseComparisonCriteria{userId=%s, comparedTimespan=%s, aggregationPolicy=%s, baseTimespan=%s, threshold=%s}"
                .formatted(username, comparedTimespan, aggregationPolicy, baseTimespan, threshold);
    }

    String username() {
        return username;
    }

    AggregationTimespan comparedTimespan() {
        return comparedTimespan;
    }

    AggregationPolicy<Category> groupingPolicy() {
        return aggregationPolicy;
    }

    AggregationTimespan baseTimespan() {
        return baseTimespan;
    }

    SpendingThreshold threshold() {
        return threshold;
    }
}
