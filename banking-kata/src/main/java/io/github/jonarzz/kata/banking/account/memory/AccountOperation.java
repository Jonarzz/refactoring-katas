package io.github.jonarzz.kata.banking.account.memory;

import java.time.Clock;
import java.time.LocalDate;

record AccountOperation(LocalDate timestamp, int amount) {

    static Clock clock = Clock.systemDefaultZone();

    static AccountOperation now(int amount) {
        return new AccountOperation(LocalDate.now(clock), amount);
    }

}
