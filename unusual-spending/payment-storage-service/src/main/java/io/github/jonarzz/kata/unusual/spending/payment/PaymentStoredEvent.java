package io.github.jonarzz.kata.unusual.spending.payment;

import java.time.OffsetDateTime;

record PaymentStoredEvent(
        String payerUsername,
        OffsetDateTime timestamp,
        String cost
) {

}
