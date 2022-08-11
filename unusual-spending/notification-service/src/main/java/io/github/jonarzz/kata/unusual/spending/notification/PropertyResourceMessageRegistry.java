package io.github.jonarzz.kata.unusual.spending.notification;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.forLanguageTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

class PropertyResourceMessageRegistry implements MessageRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyResourceMessageRegistry.class);

    private static final Locale FALLBACK_LOCALE = ENGLISH;

    private static final String MESSAGES_FILE_EXTENSION = ".properties";

    private Map<Locale, Properties> messagesByLocale;

    PropertyResourceMessageRegistry() {
        reload();
    }

    @Override
    public String get(String key, Locale locale) {
        var property = Optional.ofNullable(messagesByLocale.get(locale))
                               .orElseGet(() -> messagesByLocale.get(FALLBACK_LOCALE))
                               .getProperty(key);
        if (property == null) {
            throw new IllegalStateException(
                    "Not found message with key '%s' for neither %s nor fallback (%s) language".formatted(
                            key, locale.getDisplayLanguage(ENGLISH), FALLBACK_LOCALE.getDisplayLanguage(ENGLISH)));
        }
        return property;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void reload() {
        File i18nDirectory;
        try {
            i18nDirectory = new File(PropertyResourceMessageRegistry.class.getClassLoader()
                                                                          .getResource("i18n")
                                                                          .toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        Map<Locale, Properties> loadedMessages = new HashMap<>();
        for (var file : i18nDirectory.listFiles((dir, name) -> name.endsWith(MESSAGES_FILE_EXTENSION))) {
            var locale = forLanguageTag(file.getName()
                                            .replaceFirst(MESSAGES_FILE_EXTENSION + "$", ""));
            var messageByKey = new Properties();
            loadedMessages.put(locale, messageByKey);
            try (var inFileStream = new FileInputStream(file)) {
                messageByKey.load(inFileStream);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        messagesByLocale = loadedMessages;
        LOGGER.trace("Loaded i18n messages: {}", messagesByLocale);
    }

}
