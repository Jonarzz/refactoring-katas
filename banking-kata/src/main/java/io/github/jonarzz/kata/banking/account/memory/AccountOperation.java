package io.github.jonarzz.kata.banking.account.memory;

import java.time.Clock;
import java.time.LocalDate;

class AccountOperation {

    static Clock clock = Clock.systemDefaultZone();

    private LocalDate timestamp;
    private int amount;

    private AccountOperation(LocalDate timestamp, int amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    static AccountOperation now(int amount) {
        return new AccountOperation(LocalDate.now(clock), amount);
    }

    LocalDate timestamp() {
        return timestamp;
    }

    int amount() {
        return amount;
    }

}
