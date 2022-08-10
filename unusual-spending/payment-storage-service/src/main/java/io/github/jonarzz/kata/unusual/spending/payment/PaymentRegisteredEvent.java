package io.github.jonarzz.kata.unusual.spending.payment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

record PaymentRegisteredEvent(
        @NotBlank(message = "Payer username cannot be blank")
        String payerUsername,
        @NotNull(message = "Payment details cannot be null")
        PaymentDetails details
) {

}
