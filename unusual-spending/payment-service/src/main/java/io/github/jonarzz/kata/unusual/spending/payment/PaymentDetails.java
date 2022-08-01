package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentDetails(
        UUID id,
        Category category,
        Cost cost,
        OffsetDateTime timestamp,
        String description
) {

    public PaymentDetails {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (cost == null) {
            throw new IllegalArgumentException("Cost cannot bre null");
        }
    }

    PaymentDetails(UUID id, Category category, Cost cost, OffsetDateTime timestamp) {
        this(id, category, cost, timestamp, null);
    }

    PaymentDetails(UUID id, Category category, Cost cost) {
        this(id, category, cost, null);
    }
}
