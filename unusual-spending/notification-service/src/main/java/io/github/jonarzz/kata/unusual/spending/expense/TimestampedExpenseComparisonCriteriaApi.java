package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

public class TimestampedExpenseComparisonCriteriaApi {

    private TimestampedExpenseComparisonCriteriaApi() {

    }

    public interface WithId {

        WithComparedTimespan aggregateExpenses(AggregationTimespan comparedTimespan);
    }

    public interface WithComparedTimespan {

        WithGrouping groupedBy(AggregationPolicy<Category> aggregationPolicy);

    }

    public interface WithGrouping {

        WithBaseTimespan comparedToAggregatedExpenses(AggregationTimespan baseTimespan);
    }

    public interface WithBaseTimespan {

        TimestampedExpenseComparisonCriteria increasedByAtLeast(MultiplicationThreshold multiplicationThreshold);
    }
}
