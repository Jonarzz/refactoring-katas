package io.github.jonarzz.kata.unusual.spending.payment;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;

record Payment(UUID id, BigInteger payerId, PaymentDetails details, OffsetDateTime timestamp) {

}
