package io.github.jonarzz.kata.unusual.spending.money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public record Currency(String alphaCode, Locale locale) {

    public static final Currency USD = new Currency("USD", Locale.US);

    public static Currency create(String alphaCode, String languageTag) {
        var locale = Locale.forLanguageTag(languageTag);
        return new Currency(alphaCode, locale);
    }

    public String format(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(locale)
                           .format(price.doubleValue());
    }

    public String languageTag() {
        return locale.toLanguageTag();
    }
}
