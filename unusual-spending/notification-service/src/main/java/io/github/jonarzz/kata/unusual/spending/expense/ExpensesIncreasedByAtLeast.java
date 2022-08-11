package io.github.jonarzz.kata.unusual.spending.expense;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;

record ExpensesIncreasedByAtLeast(BigDecimal increaseMultiplicationThreshold) implements SpendingThreshold {

    private static final int DIVISION_RESULT_SCALE = 5;

    ExpensesIncreasedByAtLeast(MultiplicationThreshold increaseMultiplicationThreshold) {
        this(valueOf(increaseMultiplicationThreshold.value()));
    }

    @Override
    public boolean thresholdReached(BigDecimal base, BigDecimal verified) {
        return verified.divide(base, DIVISION_RESULT_SCALE, HALF_EVEN)
                       .compareTo(increaseMultiplicationThreshold)
               >= 0;
    }
}
