package io.github.jonarzz.kata.unusual.spending.payment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

record PaymentRegisteredEvent(
        @NotNull(message = "Payer ID cannot be null")
        @Positive(message = "ID should be a positive integer")
        Long payerId,
        @NotNull(message = "Payment details cannot be null")
        PaymentDetails details
) {

}
