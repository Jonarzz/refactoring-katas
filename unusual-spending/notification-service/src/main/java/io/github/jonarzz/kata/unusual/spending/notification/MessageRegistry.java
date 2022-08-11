package io.github.jonarzz.kata.unusual.spending.notification;

import java.util.Locale;

interface MessageRegistry {

    String get(String key, Locale locale);

    void reload();
}
