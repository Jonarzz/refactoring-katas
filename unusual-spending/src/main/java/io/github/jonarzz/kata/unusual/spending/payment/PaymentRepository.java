package io.github.jonarzz.kata.unusual.spending.payment;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

class PaymentRepository {

    Collection<Payment> getPaymentsBetween(LocalDateTime from, LocalDateTime to) {
        return List.of(); // TODO database handling
    }
}
