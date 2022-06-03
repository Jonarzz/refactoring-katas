package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.money.Cost;

import java.util.Map;

public class PaymentService {

    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public <T> Map<T, Cost> aggregateTotalExpensesBy(AggregationPolicy<T> policy, AggregationTimespan timespan) {
        return paymentRepository.getPaymentsBetween(timespan.start(), timespan.end())
                                .stream()
                                .collect(toMap(policy,
                                               Payment::price,
                                               Cost::add));
    }

}