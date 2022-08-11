package io.github.jonarzz.kata.unusual.spending.notification;

import java.util.Locale;

class CommonI18nService {

    private static final String MESSAGE_KEY_PREFIX = "common.";

    private PropertyResourceMessageRegistry messageRegistry;

    CommonI18nService(PropertyResourceMessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    String getCompanyName(Locale locale) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "company-name", locale);
    }

}
