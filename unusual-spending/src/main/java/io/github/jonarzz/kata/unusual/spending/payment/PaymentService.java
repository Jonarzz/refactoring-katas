package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigInteger;
import java.util.Map;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // TODO web API (GraphQL)

    public <T> Map<T, Cost> aggregateTotalUserExpensesBy(AggregationPolicy<T> policy,
                                                         BigInteger userId, AggregationTimespan timespan) {
        LOG.debugf("Aggregating total user expenses with %s policy for user with ID %s in %s",
                   policy, userId, timespan);
        return paymentRepository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end())
                                .stream()
                                .collect(toMap(policy,
                                               PaymentDetails::cost,
                                               Cost::add));
    }

    public void save(PaymentRegisteredEvent paymentEvent) {
        // TODO validation
        LOG.debugf("Saving %s", paymentEvent);
        paymentRepository.save(paymentEvent);
    }

}
