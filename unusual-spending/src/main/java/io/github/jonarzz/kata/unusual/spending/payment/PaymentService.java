package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.math.BigInteger;
import java.util.Map;

public class PaymentService {

    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // TODO add logging
    // TODO web API (GraphQL)

    public <T> Map<T, Cost> aggregateTotalUserExpensesBy(AggregationPolicy<T> policy,
                                                         BigInteger userId, AggregationTimespan timespan) {
        return paymentRepository.getPaymentDetailsBetween(userId, timespan.start(), timespan.end())
                                .stream()
                                .collect(toMap(policy,
                                               PaymentDetails::cost,
                                               Cost::add));
    }

    public void save(Payment payment) {
        // TODO payment saving from JMS queue
        paymentRepository.save(payment);
    }

}
