package io.github.jonarzz.kata.unusual.spending.payment;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import java.time.OffsetDateTime;
import java.util.Collection;

@GraphQLApi
public class PaymentResourceApi {

    private PaymentService paymentService;

    public PaymentResourceApi(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Query
    @Description("Get payments of given user")
    public Collection<PaymentDetails> userPayments(@Name("userId") Long userId,
                                                   @Name("from") OffsetDateTime from,
                                                   @Name("to") OffsetDateTime to) {
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }
        return paymentService.getUserPayments(userId, from, to);
    }

    private static class BadRequestException extends IllegalArgumentException {

        private BadRequestException(String message) {
            super(message);
        }
    }

}
