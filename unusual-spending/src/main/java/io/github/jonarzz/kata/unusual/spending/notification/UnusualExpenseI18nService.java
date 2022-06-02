package io.github.jonarzz.kata.unusual.spending.notification;

class UnusualExpenseI18nService {

    private MessageRegistry messageRegistry;

    UnusualExpenseI18nService(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    String getMessageBeginning() {
        return messageRegistry.get("unusual-expenses-notification.beginning");
    }

    String getMessageEnding() {
        return messageRegistry.get("unusual-expenses-notification.ending");
    }

    String getUnusualExpenseLine(String category, String amount) {
        return messageRegistry.get("unusual-expenses-notification.expense-line-template")
                              .formatted(amount, category);
    }

}
