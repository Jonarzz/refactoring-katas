package io.github.jonarzz.kata.unusual.spending.money;

import static java.util.Locale.US;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public enum Currency {

    USD(US);

    private final Locale locale;

    Currency(Locale locale) {
        this.locale = locale;
    }

    public String format(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(locale)
                           .format(price.doubleValue());
    }

}
