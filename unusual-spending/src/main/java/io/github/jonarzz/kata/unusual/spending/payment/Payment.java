package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

class Payment { // TODO database table should also have a description according to the kata requirements

    private Category category; // TODO dictionary table in database
    private Cost expense;

    Payment(Category category, Cost expense) {
        this.category = category;
        this.expense = expense;
    }

    Category category() {
        return category;
    }

    Cost price() {
        return expense;
    }

}
