package io.github.jonarzz.kata.banking.account;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(long balance) {
        super("Insufficient funds. Current balance: " + balance);
    }

}
