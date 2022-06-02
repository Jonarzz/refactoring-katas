package io.github.jonarzz.kata.unusual.spending.notification;

import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class UnusualExpenseI18NServiceTest {

    UnusualExpenseI18nService i18nService = new UnusualExpenseI18nService(new MessageRegistry());

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(ENGLISH);
    }

    @Test
    void getMessageBeginning() {
        var message = i18nService.getMessageBeginning();

        assertThat(message)
                .isEqualTo("""
                                   Hello card user!
                                   
                                   We have detected unusually high spending on your card in these categories:
                                   
                                   """);
    }

    @Test
    void getMessageEnding() {
        var message = i18nService.getMessageEnding();

        assertThat(message)
                .isEqualTo("""
                                                                      
                                   Love,
                                   The Credit Card Company""");
    }

    @Test
    void getUnusualExpenseLine() {
        var message = i18nService.getUnusualExpenseLine("travel", "$412");

        assertThat(message)
                .isEqualTo("""
                                   * You spent $412 on travel
                                   """);
    }

}