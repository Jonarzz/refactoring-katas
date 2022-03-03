package io.github.jonarzz.kata.banking.account.validation;

public class PositiveAmount {

    private int amount;

    private PositiveAmount(int amount) {
        this.amount = amount;
    }

    static PositiveAmount forDeposition(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposition amount should be positive, but was " + amount);
        }
        return new PositiveAmount(amount);
    }

    static PositiveAmount forWithdrawal(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount should be positive, but was " + amount);
        }
        return new PositiveAmount(amount);
    }

    public int value() {
        return amount;
    }

}
