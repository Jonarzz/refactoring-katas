package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

record PaymentDetails(Category category, Cost cost, String description) {

    PaymentDetails(Category category, Cost cost) {
        this(category, cost, null);
    }

    PaymentDetails describedAs(String description) {
        return new PaymentDetails(category, cost, description);
    }
}
