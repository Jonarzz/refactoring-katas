package io.github.jonarzz.kata.unusual.spending.expense;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;

class ExpensesIncreasedByAtLeast implements SpendingThreshold {

    private static final int DIVISION_RESULT_SCALE = 5;

    private BigDecimal increaseMultiplicationThreshold;

    ExpensesIncreasedByAtLeast(MultiplicationThreshold increaseMultiplicationThreshold) {
        this.increaseMultiplicationThreshold = BigDecimal.valueOf(increaseMultiplicationThreshold.value());
    }

    @Override
    public boolean thresholdReached(BigDecimal base, BigDecimal verified) {
        return verified.divide(base, DIVISION_RESULT_SCALE, HALF_EVEN)
                       .compareTo(increaseMultiplicationThreshold)
               >= 0;
    }

    @Override
    public String toString() {
        return "ExpensesIncreasedByAtLeast{multiplicationThreshold=%s}".formatted(increaseMultiplicationThreshold);
    }
}
