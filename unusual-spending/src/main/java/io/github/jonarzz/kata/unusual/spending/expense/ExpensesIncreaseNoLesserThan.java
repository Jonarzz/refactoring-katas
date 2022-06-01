package io.github.jonarzz.kata.unusual.spending.expense;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;

class ExpensesIncreaseNoLesserThan implements SpendingThreshold {

    private static final int DIVISION_RESULT_SCALE = 5;

    private BigDecimal increaseThreshold;

    private ExpensesIncreaseNoLesserThan(double increaseThreshold) {
        this.increaseThreshold = BigDecimal.valueOf(increaseThreshold);
    }

    static ExpensesIncreaseNoLesserThan proportion(double value) {
        return new ExpensesIncreaseNoLesserThan(value);
    }

    static ExpensesIncreaseNoLesserThan percentage(double value) {
        return proportion(value / 100d);
    }

    @Override
    public boolean thresholdReached(BigDecimal base, BigDecimal verified) {
        return verified.divide(base, DIVISION_RESULT_SCALE, HALF_EVEN)
                       .compareTo(increaseThreshold)
               >= 0;
    }

}
