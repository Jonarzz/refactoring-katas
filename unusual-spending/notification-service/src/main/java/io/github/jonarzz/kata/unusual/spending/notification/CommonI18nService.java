package io.github.jonarzz.kata.unusual.spending.notification;

class CommonI18nService {

    private static final String MESSAGE_KEY_PREFIX = "common.";

    private MessageRegistry messageRegistry;

    CommonI18nService(MessageRegistry messageRegistry) {
        this.messageRegistry = messageRegistry;
    }

    String getCompanyName() {
        return messageRegistry.get(MESSAGE_KEY_PREFIX + "company-name");
    }

}
