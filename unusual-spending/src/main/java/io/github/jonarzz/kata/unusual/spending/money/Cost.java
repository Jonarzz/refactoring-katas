package io.github.jonarzz.kata.unusual.spending.money;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.util.Objects;

public class Cost implements Comparable<Cost> {

    private Currency currency;
    private BigDecimal amount;

    private Cost(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public static Cost usd(int dollars, int cents) {
        if (dollars < 0 || cents < 0) {
            throw new IllegalArgumentException("Price cannot be a negative number");
        }
        return new Cost(USD, valueOf(dollars).add(valueOf(cents, 2)));
    }

    public Cost add(Cost other) {
        if (currency != other.currency) {
            throw new UnsupportedOperationException("Adding money in different currencies is unsupported at this moment");
        }
        return new Cost(currency, amount.add(other.amount));
    }

    public Currency currency() {
        return currency;
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public int compareTo(Cost other) {
        if (currency != other.currency) {
            return 0;
        }
        return amount.compareTo(other.amount);
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
        if (!(other instanceof Cost cost)) {
            return false;
        }
        return currency == cost.currency
               && amount.equals(cost.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }
}
