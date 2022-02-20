package io.github.jonarzz.kata.banking.stub;

import io.github.jonarzz.kata.banking.Account;

public class StubAccount implements Account {

    @Override
    public void deposit(int amount) {

    }

    @Override
    public void withdraw(int amount) {

    }

    @Override
    public String printStatement() {
        return """
                Date        Amount  Balance
                24.12.2015    +500      500
                23.8.2016     -100      400""";
    }

}
