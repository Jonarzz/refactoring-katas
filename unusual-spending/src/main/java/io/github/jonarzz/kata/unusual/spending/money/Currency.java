package io.github.jonarzz.kata.unusual.spending.money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class Currency {

    public static final Currency USD = new Currency("USD", Locale.US);

    private final String alphaCode;
    private final Locale locale;

    private Currency(String alphaCode, Locale locale) {
        this.alphaCode = alphaCode;
        this.locale = locale;
    }

    public static Currency create(String alphaCode, String languageTag) {
        var locale = Locale.forLanguageTag(languageTag);
        return new Currency(alphaCode, locale);
    }

    public String format(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(locale)
                           .format(price.doubleValue());
    }

    public String alphaCode() {
        return alphaCode;
    }

    public String languageTag() {
        return locale.toLanguageTag();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Currency currency)) {
            return false;
        }
        return alphaCode.equals(currency.alphaCode)
               && locale.equals(currency.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alphaCode, locale);
    }

    @Override
    public String toString() {
        return "Currency{alphaCode='%s', locale=%s}".formatted(alphaCode, locale);
    }
}
