package io.github.jonarzz.kata.unusual.spending.payment;

import java.util.function.Function;

public interface AggregationPolicy<T> extends Function<PaymentDetails, T> {

    static AggregationPolicy<Category> category() {
        return ByCategory.INSTANCE;
    }

    class ByCategory implements AggregationPolicy<Category> {

        private static final ByCategory INSTANCE = new ByCategory();

        @Override
        public Category apply(PaymentDetails paymentDetails) {
            return paymentDetails.category();
        }

        @Override
        public String toString() {
            return "ByCategory";
        }
    }

}
