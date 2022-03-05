package io.github.jonarzz.kata.banking.account;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(long balance, int amount) {
        super("Insufficient funds. Current balance: " + balance + ", requested amount: " + amount);
    }

}
