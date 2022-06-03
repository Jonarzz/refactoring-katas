package io.github.jonarzz.kata.unusual.spending.payment;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

class PaymentRepository {

    // TODO payments saving

    Collection<Payment> getUserPaymentsBetween(BigInteger userId, LocalDateTime from, LocalDateTime to) {
        return List.of(); // TODO database handling
    }
}
