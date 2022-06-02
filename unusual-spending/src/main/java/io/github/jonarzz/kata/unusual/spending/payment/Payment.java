package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

class Payment {

    private Category category;
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
