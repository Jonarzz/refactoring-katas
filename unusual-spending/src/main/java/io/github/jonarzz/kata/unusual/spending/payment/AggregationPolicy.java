package io.github.jonarzz.kata.unusual.spending.payment;

import java.util.function.Function;

public interface AggregationPolicy<T> extends Function<PaymentDetails, T> {

    static AggregationPolicy<Category> category() {
        return PaymentDetails::category;
    }

}
