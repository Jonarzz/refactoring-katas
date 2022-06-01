package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.Currency.USD;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.util.Objects;

public class Expense implements Comparable<Expense> {

    private Currency currency;
    private BigDecimal amount;

    private Expense(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public static Expense usd(int dollars, int cents) {
        if (dollars < 0 || cents < 0) {
            throw new IllegalArgumentException("Price cannot be a negative number");
        }
        return new Expense(USD, valueOf(dollars).add(valueOf(cents, 2)));
    }

    public Expense add(Expense other) {
        if (other.currency != currency) {
            throw new UnsupportedOperationException("Adding prices in different currencies is unsupported at this moment");
        }
        return new Expense(currency, amount.add(other.amount));
    }

    @Override
    public String toString() {
        return currency.format(amount);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Expense expense)) {
            return false;
        }
        return currency == expense.currency
               && amount.equals(expense.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }

    @Override
    public int compareTo(Expense other) {
        if (currency != other.currency) {
            return 0;
        }
        return amount.compareTo(other.amount);
    }

    public ThresholdMatcher comparedTo(Expense expense) {
        return new ThresholdMatcher(expense);
    }

    public class ThresholdMatcher {

        private Expense comparisonBase;

        private ThresholdMatcher(Expense comparisonBase) {
            this.comparisonBase = comparisonBase;
        }

        public boolean satisfiesThreshold(SpendingThreshold threshold) {
            if (comparisonBase.currency != currency) {
                return false;
            }
            return threshold.thresholdReached(comparisonBase.amount, amount);
        }

    }
}
