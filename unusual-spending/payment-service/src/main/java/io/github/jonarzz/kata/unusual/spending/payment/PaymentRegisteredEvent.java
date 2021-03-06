package io.github.jonarzz.kata.unusual.spending.payment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

record PaymentRegisteredEvent(
        @NotNull(message = "Event ID cannot be null")
        UUID id,
        @NotNull(message = "Payer ID cannot be null")
        @Positive(message = "ID should be a positive integer")
        Long payerId,
        @NotNull(message = "Payment details cannot be null")
        PaymentDetails details
) {

}
