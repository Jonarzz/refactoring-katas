package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

record PaymentDetails(Category category, Cost cost, String description) {

    PaymentDetails {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (cost == null) {
            throw new IllegalArgumentException("Cost cannot bre null");
        }
    }

    PaymentDetails(Category category, Cost cost) {
        this(category, cost, null);
    }
}
