package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.util.Optional;

class PaymentDetails {

    private final Category category;
    private final Cost cost;

    private String description;

    PaymentDetails(Category category, Cost expense) {
        this.category = category;
        cost = expense;
    }

    void describedAs(String description) {
        this.description = description;
    }

    Category category() {
        return category;
    }

    Cost cost() {
        return cost;
    }

    Optional<String> description() {
        return Optional.ofNullable(description);
    }
}
