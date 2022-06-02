package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

import java.util.Objects;

public class CategorizedExpense implements Comparable<CategorizedExpense> {

    private Category category;
    private Cost amount;

    CategorizedExpense(Category category, Cost amount) {
        this.category = category;
        this.amount = amount;
    }

    public String category() {
        return category.toString()
                       .toLowerCase();
    }

    public String amount() {
        return amount.toString();
    }

    @Override
    public int compareTo(CategorizedExpense other) {
        return amount.compareTo(other.amount);
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
               && amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, amount);
    }

    @Override
    public String toString() {
        return "CategorizedExpense{category=%s, amount=%s}".formatted(category, amount);
    }
}
