package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.Timespan;

class TimestampedExpenseComparison {

    final Timespan comparedTimespan;

    private TimestampedExpenseComparison(Timespan comparedTimespan) {
        this.comparedTimespan = comparedTimespan;
    }

    static TimestampedExpenseComparison expenses(Timespan comparedTimespan) {
        return new TimestampedExpenseComparison(comparedTimespan);
    }

    public WithGrouping groupedBy(GroupingPolicy<Category> groupingPolicy) {
        return new WithGrouping(comparedTimespan, groupingPolicy);
    }

    static class WithGrouping extends TimestampedExpenseComparison {

        final GroupingPolicy<Category> groupingPolicy;

        private WithGrouping(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy) {
            super(comparedTimespan);
            this.groupingPolicy = groupingPolicy;
        }

        WithTimestamps comparedToExpenses(Timespan baseTimespan) {
            return new WithTimestamps(comparedTimespan, groupingPolicy, baseTimespan);
        }
    }

    static class WithTimestamps extends WithGrouping {

        final Timespan baseTimespan;

        private WithTimestamps(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy, Timespan baseTimespan) {
            super(comparedTimespan, groupingPolicy);
            this.baseTimespan = baseTimespan;
        }

        WithThreshold matching(SpendingThreshold threshold) {
            return new WithThreshold(comparedTimespan, groupingPolicy, baseTimespan, threshold);
        }
    }

    static class WithThreshold extends WithTimestamps {

        private final SpendingThreshold threshold;

        private WithThreshold(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy,
                              Timespan baseTimespan, SpendingThreshold threshold) {
            super(comparedTimespan, groupingPolicy, baseTimespan);
            this.threshold = threshold;
        }

        Timespan comparedTimespan() {
            return comparedTimespan;
        }

        GroupingPolicy<Category> groupingPolicy() {
            return groupingPolicy;
        }

        Timespan baseTimespan() {
            return baseTimespan;
        }

        SpendingThreshold threshold() {
            return threshold;
        }
    }

}
