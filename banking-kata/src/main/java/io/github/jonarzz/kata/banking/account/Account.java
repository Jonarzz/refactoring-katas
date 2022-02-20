package io.github.jonarzz.kata.banking.account;

public interface Account {

    void deposit(int amount) throws IllegalArgumentException;

    void withdraw(int amount) throws InsufficientFundsException, IllegalArgumentException;

    String printStatement();

}
