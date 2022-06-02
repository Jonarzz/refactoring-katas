package io.github.jonarzz.kata.unusual.spending.notification;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.forLanguageTag;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

class MessageRegistry {

    private static final Locale FALLBACK_LOCALE = ENGLISH;

    private static final String MESSAGES_FILE_EXTENSION = ".properties";
    private static final String KEY_AND_MESSAGE_SEPARATOR = "=";

    private Map<Locale, Properties> messagesByLocale;

    MessageRegistry() {
        reload();
    }

    @SuppressWarnings("ConstantConditions")
    MessageRegistry reload() {
        File i18nDirectory;
        try {
            i18nDirectory = new File(MessageRegistry.class.getClassLoader()
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
            try (var lines = Files.lines(file.toPath())) {
                lines.map(line -> line.split(KEY_AND_MESSAGE_SEPARATOR))
                     .filter(split -> split.length == 2)
                     .forEach(keyAndMessage -> messageByKey.put(keyAndMessage[0].strip(),
                                                                keyAndMessage[1].strip()
                                                                                .replaceAll("(^\")|(\"$)", "")
                                                                                .replace("\\n", "\n")));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        messagesByLocale = loadedMessages;
        return this;
    }

    String get(String key) {
        var locale = Locale.getDefault();
        var localeMessagesByKey = messagesByLocale.get(locale);
        if (localeMessagesByKey == null) {
            locale = FALLBACK_LOCALE;
            localeMessagesByKey = messagesByLocale.get(locale);
        }
        var property = localeMessagesByKey.getProperty(key);
        if (property == null) {
            throw new IllegalStateException("Not found message with key '" + key + "' for " + locale);
        }
        return property;
    }

}
