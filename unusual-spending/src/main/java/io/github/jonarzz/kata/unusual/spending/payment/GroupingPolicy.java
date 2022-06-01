package io.github.jonarzz.kata.unusual.spending.payment;

import java.util.function.Function;

public interface GroupingPolicy<T> extends Function<Payment, T> {

}
