package io.github.jonarzz.kata.unusual.spending.money;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import io.smallrye.graphql.api.AdaptWith;
import io.smallrye.graphql.api.Adapter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record Currency(
        String alphaCode,
        @AdaptWith(CurrencyLocaleAdapter.class)
        Locale locale
) {

    public static final Currency USD = new Currency("USD", Locale.US);

    private static final Map<String, Currency> ALPHA_CODE_TO_CURRENCY = Stream.of(USD)
                                                                              .collect(toMap(Currency::alphaCode,
                                                                                             identity()));

    public Currency {
        if (alphaCode == null) {
            throw new IllegalArgumentException("Currency alpha code cannot be null");
        }
        if (alphaCode.length() != 3) {
            throw new IllegalArgumentException("Currency alpha code should have exactly 3 characters");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Currency locale cannot be null");
        }
    }

    public static Currency create(String alphaCode, String languageTag) {
        var locale = CurrencyLocaleAdapter.localeFrom(languageTag);
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
        return CurrencyLocaleAdapter.languageTagFrom(locale);
    }

    public static class CurrencyLocaleAdapter implements Adapter<Locale, String> {

        @Override
        public String to(Locale locale) {
            return languageTagFrom(locale);
        }

        @Override
        public Locale from(String string) {
            return localeFrom(string);
        }

        private static String languageTagFrom(Locale locale) {
            return locale.toLanguageTag();
        }

        private static Locale localeFrom(String languageTag) {
            return Locale.forLanguageTag(languageTag);
        }
    }
}
