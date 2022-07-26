package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigInteger;
import java.util.Map;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    private Validator validator;
    private PaymentRepository paymentRepository;

    public PaymentService(Validator validator, PaymentRepository paymentRepository) {
        this.validator = validator;
        this.paymentRepository = paymentRepository;
    }

    // TODO web API (GraphQL) in a different MS

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

    @ActivateRequestContext
    public void save(PaymentRegisteredEvent paymentEvent) {
        var validationErrors = validator.validate(paymentEvent);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Event validation failed. "
                                               + validationErrors.stream()
                                                                 .map(ConstraintViolation::getMessage)
                                                                 .collect(joining(". "))
                                               + ". Validated object: "
                                               + paymentEvent);
        }
        LOG.debugf("Saving %s", paymentEvent);
        if (!paymentRepository.save(paymentEvent)) {
            LOG.infof("Event with ID %s has been saved already", paymentEvent.id());
        }
    }

}
