package io.github.jonarzz.kata.banking;

public interface Account {

    void deposit(int amount);

    void withdraw(int amount);

    String printStatement();

}
