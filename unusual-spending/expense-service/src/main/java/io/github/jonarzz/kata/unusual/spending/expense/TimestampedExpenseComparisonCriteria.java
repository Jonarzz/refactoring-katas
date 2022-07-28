package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

public class TimestampedExpenseComparisonCriteria implements
        TimestampedExpenseComparisonCriteriaApi.WithId,
        TimestampedExpenseComparisonCriteriaApi.WithComparedTimespan,
        TimestampedExpenseComparisonCriteriaApi.WithGrouping,
        TimestampedExpenseComparisonCriteriaApi.WithBaseTimespan {

    private long userId;
    private AggregationTimespan comparedTimespan;
    private AggregationPolicy<Category> aggregationPolicy;
    private AggregationTimespan baseTimespan;
    private SpendingThreshold threshold;

    private TimestampedExpenseComparisonCriteria(long userId) {
        this.userId = userId;
    }

    public static TimestampedExpenseComparisonCriteriaApi.WithId forUserId(long userId) {
        return new TimestampedExpenseComparisonCriteria(userId);
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
                .formatted(userId, comparedTimespan, aggregationPolicy, baseTimespan, threshold);
    }

    long userId() {
        return userId;
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
