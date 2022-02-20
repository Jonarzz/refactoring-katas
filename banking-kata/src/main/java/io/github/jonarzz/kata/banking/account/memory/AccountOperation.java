package io.github.jonarzz.kata.banking.account.memory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;

class AccountOperation {

    static Clock clock = Clock.systemDefaultZone();

    private TemporalAccessor timestamp;
    private int amount;

    private AccountOperation(TemporalAccessor timestamp, int amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    static AccountOperation now(int amount) {
        return new AccountOperation(LocalDate.now(clock), amount);
    }

    TemporalAccessor timestamp() {
        return timestamp;
    }

    int amount() {
        return amount;
    }

}
