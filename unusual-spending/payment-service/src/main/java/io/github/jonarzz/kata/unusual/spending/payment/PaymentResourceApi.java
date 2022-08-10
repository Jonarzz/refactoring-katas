package io.github.jonarzz.kata.unusual.spending.payment;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@GraphQLApi
public class PaymentResourceApi {

    private PaymentService paymentService;

    public PaymentResourceApi(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Query
    @Description("Get payments of given user")
    public Collection<PaymentDetails> userPayments(@Name("username") String username,
                                                   @Name("from") OffsetDateTime from,
                                                   @Name("to") OffsetDateTime to) {
        if (username == null) {
            throw new BadRequestException("Username is required");
        }
        return paymentService.getUserPayments(username, from, to);
    }

    @Query
    @Description("Get details of given payment")
    public Optional<PaymentDetails> paymentDetails(@Name("paymentId") String paymentId) {
        if (paymentId == null) {
            throw new BadRequestException("Payment ID is required");
        }
        return paymentService.getPaymentDetails(UUID.fromString(paymentId));
    }

    private static class BadRequestException extends IllegalArgumentException {

        private BadRequestException(String message) {
            super(message);
        }
    }

}
