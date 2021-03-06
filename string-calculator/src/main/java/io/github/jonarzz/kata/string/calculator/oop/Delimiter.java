package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.String.join;
import static java.util.regex.Pattern.quote;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

class Delimiter {

    private static final String DELIMITER_LINE_PREFIX = "//";
    private static final Pattern EXTENDED_DELIMITER_PATTERN = Pattern.compile("\\[([^]]+)]");

    private static final String DEFAULT_REGEX = "[\\n,]";

    private final String regex;

    private Delimiter(String regex) {
        this.regex = regex;
    }

    private Delimiter() {
        this(DEFAULT_REGEX);
    }

    static Delimiter notCustomized() {
        return new Delimiter();
    }

    static Delimiter fromLine(String line) {
        if (!line.startsWith(DELIMITER_LINE_PREFIX)) {
            return new Delimiter();
        }
        var delimiterValue = line.replaceFirst("^" + DELIMITER_LINE_PREFIX, "");
        var matcher = EXTENDED_DELIMITER_PATTERN.matcher(delimiterValue);
        if (!matcher.find()) {
            return new Delimiter(quote(delimiterValue));
        }
        Set<String> delimiters = new HashSet<>();
        do {
            delimiters.add(quote(matcher.group(1)));
        } while (matcher.find());
        return new Delimiter(join("|", delimiters));
    }

    String[] split(String value) {
        return value.split(regex, -1);
    }

    boolean isCustomized() {
        return !DEFAULT_REGEX.equals(regex);
    }

}
