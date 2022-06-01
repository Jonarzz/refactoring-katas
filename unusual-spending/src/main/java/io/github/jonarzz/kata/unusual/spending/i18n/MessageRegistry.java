package io.github.jonarzz.kata.unusual.spending.i18n;

import static java.util.Locale.forLanguageTag;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class MessageRegistry {

    public static final String MESSAGES_FILE_EXTENSION = ".properties";
    public static final String KEY_AND_MESSAGE_SEPARATOR = "=";

    private Map<Locale, Map<String, String>> keyGroupedMessagesByLocale;

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
        Map<Locale, Map<String, String>> loadedMessages = new HashMap<>();
        for (var file : i18nDirectory.listFiles((dir, name) -> name.endsWith(MESSAGES_FILE_EXTENSION))) {
            var locale = forLanguageTag(file.getName()
                                            .replaceFirst(MESSAGES_FILE_EXTENSION + "$", ""));
            var messageByKey = new HashMap<String, String>();
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
        keyGroupedMessagesByLocale = loadedMessages;
        return this;
    }

    String get(String key) {
        var localeMessagesByKey = keyGroupedMessagesByLocale.get(Locale.getDefault());
        if (localeMessagesByKey == null) {
            return null;
        }
        return localeMessagesByKey.get(key);
    }

}
