package io.github.jonarzz.kata.unusual.spending.expense;

import java.math.BigDecimal;

interface SpendingThreshold {

    boolean thresholdReached(BigDecimal base, BigDecimal verified);

}
