package io.github.jonarzz.kata.unusual.spending.notification;

import java.util.Locale;

class UnusualExpenseI18nService {

    private static final String MESSAGE_KEY_PREFIX = "unusual-expenses-notification.";

    private PropertyResourceMessageRegistry messageRegistry;

    UnusualExpenseI18nService(PropertyResourceMessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    String getMessageBeginningGreeting(Locale locale) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "beginning-greeting", locale);
    }

    String getExpenseLinesPrefix(Locale locale) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "expense-lines-prefix", locale);
    }

    String getUnusualExpenseLine(Locale locale, String category, String amount) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "expense-line-template", locale)
                              .formatted(amount, category);
    }

    String getMessageEndingGreeting(Locale locale) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "ending-greeting", locale);
    }

}
