package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

import java.math.BigInteger;

public class TimestampedExpenseComparison implements
        TimestampedExpenseComparisonApi.WithId,
        TimestampedExpenseComparisonApi.WithComparedTimespan,
        TimestampedExpenseComparisonApi.WithGrouping,
        TimestampedExpenseComparisonApi.WithBaseTimespan {

    private BigInteger userId;
    private AggregationTimespan comparedTimespan;
    private AggregationPolicy<Category> aggregationPolicy;
    private AggregationTimespan baseTimespan;
    private SpendingThreshold threshold;

    private TimestampedExpenseComparison(BigInteger userId) {
        this.userId = userId;
    }

    public static TimestampedExpenseComparisonApi.WithId forUserId(BigInteger userId) {
        return new TimestampedExpenseComparison(userId);
    }

    @Override
    public TimestampedExpenseComparisonApi.WithComparedTimespan aggregateExpenses(AggregationTimespan comparedTimespan) {
        this.comparedTimespan = comparedTimespan;
        return this;
    }

    @Override
    public TimestampedExpenseComparisonApi.WithGrouping groupedBy(AggregationPolicy<Category> aggregationPolicy) {
        this.aggregationPolicy = aggregationPolicy;
        return this;
    }

    @Override
    public TimestampedExpenseComparisonApi.WithBaseTimespan comparedToAggregatedExpenses(AggregationTimespan baseTimespan) {
        this.baseTimespan = baseTimespan;
        return this;
    }

    @Override
    public TimestampedExpenseComparison increasedByAtLeast(ThresholdValue thresholdValue) {
        threshold = new ExpensesIncreasedByAtLeast(thresholdValue);
        return this;
    }

    BigInteger userId() {
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
