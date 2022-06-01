package io.github.jonarzz.kata.unusual.spending.i18n;

import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class I18nServiceTest {

    I18nService i18nService = new I18nService();

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(ENGLISH);
    }

    @Test
    void getMessageWithoutParams_messageExists() {
        var message = i18nService.getMessage("unusual-expenses-notification.end");

        assertThat(message)
                .hasValue("""
                                                                      
                                   Love,
                                   The Credit Card Company""");
    }

    @Test
    void getMessageWithoutParams_messageDoesNotExist() {
        var message = i18nService.getMessage("some non existent key");

        assertThat(message)
                .isEmpty();
    }

    @Test
    void getMessageWithParams_messageExists() {
        var message = i18nService.getMessage("unusual-expenses-notification.expense-line-template",
                                             "$412", "travel");

        assertThat(message)
                .hasValue("""
                                  * You spent $412 on travel
                                  """);
    }

    @Test
    void getMessageWithParams_messageDoesNotExist() {
        var message = i18nService.getMessage("some non existent key", 1, 2, "param");

        assertThat(message)
                .isEmpty();
    }

}