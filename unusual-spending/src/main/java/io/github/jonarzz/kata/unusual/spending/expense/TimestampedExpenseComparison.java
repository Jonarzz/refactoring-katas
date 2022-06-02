package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.Category;
import io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.Timespan;

public class TimestampedExpenseComparison {

    final Timespan comparedTimespan;

    private TimestampedExpenseComparison(Timespan comparedTimespan) {
        this.comparedTimespan = comparedTimespan;
    }

    public WithGrouping groupedBy(GroupingPolicy<Category> groupingPolicy) {
        return new WithGrouping(comparedTimespan, groupingPolicy);
    }

    public static class WithGrouping extends TimestampedExpenseComparison {

        final GroupingPolicy<Category> groupingPolicy;

        private WithGrouping(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy) {
            super(comparedTimespan);
            this.groupingPolicy = groupingPolicy;
        }

        public WithTimestamps comparedToExpenses(Timespan baseTimespan) {
            return new WithTimestamps(comparedTimespan, groupingPolicy, baseTimespan);
        }
    }

    public static class WithTimestamps extends WithGrouping {

        final Timespan baseTimespan;

        private WithTimestamps(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy, Timespan baseTimespan) {
            super(comparedTimespan, groupingPolicy);
            this.baseTimespan = baseTimespan;
        }

        public WithThreshold increasedByAtLeast(ThresholdValue thresholdValue) {
            var threshold = new ExpensesIncreasedByAtLeast(thresholdValue);
            return matching(threshold);
        }

        WithThreshold matching(SpendingThreshold threshold) {
            return new WithThreshold(comparedTimespan, groupingPolicy, baseTimespan, threshold);
        }
    }

    public static class WithThreshold extends WithTimestamps {

        private final SpendingThreshold threshold;

        private WithThreshold(Timespan comparedTimespan, GroupingPolicy<Category> groupingPolicy,
                              Timespan baseTimespan, SpendingThreshold threshold) {
            super(comparedTimespan, groupingPolicy, baseTimespan);
            this.threshold = threshold;
        }

        public static TimestampedExpenseComparison expenses(Timespan comparedTimespan) {
            return new TimestampedExpenseComparison(comparedTimespan);
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
