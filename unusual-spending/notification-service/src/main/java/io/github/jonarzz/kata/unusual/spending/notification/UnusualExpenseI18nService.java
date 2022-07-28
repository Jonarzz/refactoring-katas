package io.github.jonarzz.kata.unusual.spending.notification;

class UnusualExpenseI18nService {

    private static final String MESSAGE_KEY_PREFIX = "unusual-expenses-notification.";

    private MessageRegistry messageRegistry;

    UnusualExpenseI18nService(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    String getMessageBeginningGreeting() {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "beginning-greeting");
    }

    String getExpenseLinesPrefix() {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "expense-lines-prefix");
    }

    String getUnusualExpenseLine(String category, String amount) {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "expense-line-template")
                              .formatted(amount, category);
    }

    String getMessageEndingGreeting() {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "ending-greeting");
    }

}
