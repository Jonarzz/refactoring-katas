package io.github.jonarzz.kata.unusual.spending.i18n;

import java.util.Optional;

public class I18nService {

    // TODO cover with facade?

    private MessageRegistry messageRegistry = new MessageRegistry();

    public Optional<String> getMessage(String key) {
        return Optional.ofNullable(messageRegistry.get(key));
    }

    public Optional<String> getMessage(String key, Object... params) {
        return getMessage(key)
                .map(message -> message.formatted(params));
    }

}
