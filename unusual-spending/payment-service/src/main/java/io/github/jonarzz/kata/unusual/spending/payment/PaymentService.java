package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    private Validator validator;
    private PaymentRepository paymentRepository;

    public PaymentService(Validator validator, PaymentRepository paymentRepository) {
        this.validator = validator;
        this.paymentRepository = paymentRepository;
    }

    // TODO default visibility after moving all modules to MS
    public <T> Map<T, Cost> aggregateTotalUserExpensesBy(AggregationPolicy<T> policy,
                                                         String username, AggregationTimespan timespan) {
        LOG.debugf("Aggregating total user expenses with %s policy for user %s in %s",
                   policy, username, timespan);
        return paymentRepository.getPaymentDetailsBetween(username, timespan.start(), timespan.end())
                                .stream()
                                .collect(toMap(policy,
                                               PaymentDetails::cost,
                                               Cost::add));
    }

    Collection<PaymentDetails> getUserPayments(String username, OffsetDateTime from, OffsetDateTime to) {
        LOG.debugf("Retrieving payments of user %s between %s and %s", username, from, to);
        return paymentRepository.getPaymentDetailsBetween(username, from, to);
    }

    Optional<PaymentDetails> getPaymentDetails(UUID paymentId) {
        LOG.debugf("Retrieving details of payment with ID: %s", paymentId);
        return paymentRepository.getPaymentDetails(paymentId);
    }

    @ActivateRequestContext
    void save(PaymentRegisteredEvent paymentEvent) {
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
            LOG.infof("Event with ID %s has been saved already", paymentEvent.details()
                                                                             .id());
        }
    }
}
