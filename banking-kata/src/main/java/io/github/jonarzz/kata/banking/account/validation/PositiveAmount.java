package io.github.jonarzz.kata.banking.account.validation;

public record PositiveAmount(int value) {

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

}
