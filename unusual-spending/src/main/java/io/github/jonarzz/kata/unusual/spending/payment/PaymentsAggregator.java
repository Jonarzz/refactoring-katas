package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.stream.Collectors.toMap;

import io.github.jonarzz.kata.unusual.spending.expense.Expense;

import java.util.Map;

public class PaymentsAggregator {

    private PaymentRepository paymentRepository;

    public PaymentsAggregator(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public <T> Map<T, Expense> calculateTotalExpensesGroupedBy(GroupingPolicy<T> groupingPolicy, Timespan timespan) {
        return paymentRepository.getPaymentsBetween(timespan.from(), timespan.to())
                                .stream()
                                .collect(toMap(groupingPolicy,
                                               Payment::price,
                                               Expense::add));
    }

}
