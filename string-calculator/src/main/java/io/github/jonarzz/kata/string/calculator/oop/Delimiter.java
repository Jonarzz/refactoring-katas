package io.github.jonarzz.kata.string.calculator.oop;

import static java.util.regex.Pattern.quote;

class Delimiter {

    private static final String DELIMITER_LINE_PREFIX = "//";

    private static final String DEFAULT_REGEX = "[\\n,]";

    private final String regex;

    private Delimiter() {
        this(DEFAULT_REGEX);
    }

    private Delimiter(String regex) {
        this.regex = regex;
    }

    static Delimiter notCustomized() {
        return new Delimiter();
    }

    static Delimiter fromLine(String line) {
        if (!line.startsWith(DELIMITER_LINE_PREFIX)) {
            return new Delimiter();
        }
        var unescapedRegex = line.replaceFirst("^" + DELIMITER_LINE_PREFIX, "");
        return new Delimiter(quote(unescapedRegex));
    }

    String[] split(String value) {
        return value.split(regex, -1);
    }

    boolean isCustomized() {
        return !DEFAULT_REGEX.equals(regex);
    }

}
