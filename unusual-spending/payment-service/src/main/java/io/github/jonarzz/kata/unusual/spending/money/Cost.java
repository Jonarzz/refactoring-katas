package io.github.jonarzz.kata.unusual.spending.money;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Objects;

public class Cost implements Comparable<Cost> {

    @Valid
    private Currency currency;
    private BigDecimal amount;

    private Cost(Currency currency, BigDecimal amount) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (amount.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.currency = currency;
        this.amount = amount;
    }

    public static Cost create(int basicUnits, int cents, Currency currency) {
        if (basicUnits < 0 || cents < 0) {
            throw new IllegalArgumentException("Cost cannot be a negative number");
        }
        return new Cost(currency, valueOf(basicUnits).add(valueOf(cents, 2)));
    }

    public static Cost create(double amount, Currency currency) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cost cannot be a negative number");
        }
        return new Cost(currency, valueOf(amount));
    }

    public static Cost usd(int dollars, int cents) {
        return create(dollars, cents, USD);
    }

    public static Cost usd(double amount) {
        return create(amount, USD);
    }

    public Cost add(Cost other) {
        if (!currency.equals(other.currency)) {
            throw new UnsupportedOperationException("Adding money in different currencies is unsupported at the moment");
        }
        return new Cost(currency, amount.add(other.amount));
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public int compareTo(Cost other) {
        if (!currency.equals(other.currency)) {
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
        return currency.equals(cost.currency)
               && amount.equals(cost.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }
}
