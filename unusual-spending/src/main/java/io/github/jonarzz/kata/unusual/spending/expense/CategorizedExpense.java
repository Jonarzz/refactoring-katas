package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.payment.Category;

import java.util.Objects;

class CategorizedExpense implements Comparable<CategorizedExpense> {

    private Category category;
    private Expense expense;

    CategorizedExpense(Category category, Expense expense) {
        this.category = category;
        this.expense = expense;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CategorizedExpense that)) {
            return false;
        }
        return category == that.category
               && expense.equals(that.expense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, expense);
    }

    @Override
    public int compareTo(CategorizedExpense other) {
        return expense.compareTo(other.expense);
    }

    String category() {
        return category.toString().toLowerCase();
    }

    String expense() {
        return expense.toString();
    }

}
