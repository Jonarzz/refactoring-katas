package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.time.OffsetDateTime;

public record PaymentDetails(
        Category category,
        Cost cost,
        OffsetDateTime timestamp,
        String description
) {

    public PaymentDetails {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (cost == null) {
            throw new IllegalArgumentException("Cost cannot bre null");
        }
    }

    PaymentDetails(Category category, Cost cost, OffsetDateTime timestamp) {
        this(category, cost, timestamp, null);
    }

    PaymentDetails(Category category, Cost cost) {
        this(category, cost, null);
    }
}
