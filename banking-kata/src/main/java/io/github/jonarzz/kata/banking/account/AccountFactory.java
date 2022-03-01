package io.github.jonarzz.kata.banking.account;

public interface AccountFactory<S> {

    Account<S> createAccount();

}
