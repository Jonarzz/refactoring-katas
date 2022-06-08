package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.util.Optional;

class Payment {

    private final Category category;
    private final Cost cost;

    private String description;

    Payment(Category category, Cost expense) {
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
