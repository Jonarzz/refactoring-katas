package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

class Payment {

    private Category category;
    private Cost cost;

    Payment(Category category, Cost expense) {
        this.category = category;
        cost = expense;
    }

    Category category() {
        return category;
    }

    Cost cost() {
        return cost;
    }

}
