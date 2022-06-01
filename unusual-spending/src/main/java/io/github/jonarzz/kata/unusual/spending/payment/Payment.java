package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.expense.Expense;

class Payment {

    private Category category;
    private Expense expense;

    Payment(Category category, Expense expense) {
        this.category = category;
        this.expense = expense;
    }

    Category category() {
        return category;
    }

    Expense price() {
        return expense;
    }

}
