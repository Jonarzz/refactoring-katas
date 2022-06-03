package io.github.jonarzz.kata.unusual.spending.payment;

import java.util.function.Function;

public interface AggregationPolicy<T> extends Function<Payment, T> {

    static AggregationPolicy<Category> category() {
        return Payment::category;
    }

}
