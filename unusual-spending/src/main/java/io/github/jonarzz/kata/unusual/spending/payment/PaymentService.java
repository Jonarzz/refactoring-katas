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

    public <T> Map<T, Cost> aggregateTotalUserExpensesBy(AggregationPolicy<T> policy,
                                                         BigInteger userId, AggregationTimespan timespan) {
        return paymentRepository.getUserPaymentsBetween(userId, timespan.start(), timespan.end())
                                .stream()
                                .collect(toMap(policy,
                                               Payment::cost,
                                               Cost::add));
    }

}
