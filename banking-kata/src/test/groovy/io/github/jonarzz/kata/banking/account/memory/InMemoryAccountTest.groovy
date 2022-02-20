package io.github.jonarzz.kata.banking.account.memory

import io.github.jonarzz.kata.banking.AbstractAccountTest
import io.github.jonarzz.kata.banking.account.Account

import java.time.Clock

class InMemoryAccountTest extends AbstractAccountTest {

    @Override
    protected Account createAccount(Clock clock) {
        AccountOperation.clock = clock;
        return new InMemoryAccount()
    }

}
