package io.github.jonarzz.kata.banking.account;

public interface Account<S> {

    void deposit(int amount) throws IllegalArgumentException;

    void withdraw(int amount) throws InsufficientFundsException, IllegalArgumentException;

     S printStatement();

}
