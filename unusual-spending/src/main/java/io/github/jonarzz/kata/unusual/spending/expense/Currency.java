package io.github.jonarzz.kata.unusual.spending.expense;

import static java.util.Locale.US;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

enum Currency {

    USD(US);

    private final Locale locale;

    Currency(Locale locale) {
        this.locale = locale;
    }

    String format(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(locale)
                           .format(price.doubleValue());
    }

}
