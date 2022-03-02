package io.github.jonarzz.kata.banking.account.validation;

import io.github.jonarzz.kata.banking.account.Account;
import io.github.jonarzz.kata.banking.account.InsufficientFundsException;

public abstract class ValidatedAccount<S> implements Account<S> {

    protected abstract void deposit(PositiveAmount positiveAmount);

    protected abstract void withdraw(PositiveAmount positiveAmount) throws InsufficientFundsException;

    @Override
    public final void deposit(int amount) throws IllegalArgumentException {
        var validAmount = PositiveAmount.forDeposition(amount);
        deposit(validAmount);
    }

    @Override
    public final void withdraw(int amount) throws InsufficientFundsException, IllegalArgumentException {
        var validAmount = PositiveAmount.forWithdrawal(amount);
        withdraw(validAmount);
    }

}
