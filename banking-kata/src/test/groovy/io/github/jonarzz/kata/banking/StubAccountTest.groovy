package io.github.jonarzz.kata.banking

import io.github.jonarzz.kata.banking.stub.StubAccount

import java.time.Clock

class StubAccountTest extends AbstractAccountTest {

    @Override
    protected Account createAccount(Clock clock) {
        return new StubAccount()
    }

}