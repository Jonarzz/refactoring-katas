package io.github.jonarzz.kata.unusual.spending.money;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record Currency(String alphaCode, Locale locale) {

    public static final Currency USD = new Currency("USD", Locale.US);

    private static final Map<String, Currency> ALPHA_CODE_TO_CURRENCY = Stream.of(USD)
                                                                              .collect(toMap(Currency::alphaCode,
                                                                                             identity()));

    public static Currency create(String alphaCode, String languageTag) {
        var locale = Locale.forLanguageTag(languageTag);
        return new Currency(alphaCode, locale);
    }

    public static Currency getInstance(String alphaCode) {
        return Optional.ofNullable(ALPHA_CODE_TO_CURRENCY.get(alphaCode))
                       .orElseThrow(() -> new IllegalArgumentException("Not found a currency instance for alpha code "
                                                                       + alphaCode));
    }

    public String format(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(locale)
                           .format(price.doubleValue());
    }

    public String languageTag() {
        return locale.toLanguageTag();
    }
}
