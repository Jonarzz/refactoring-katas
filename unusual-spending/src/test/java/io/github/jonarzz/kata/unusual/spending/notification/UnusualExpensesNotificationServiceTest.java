package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.ENTERTAINMENT;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.GOLF;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.RESTAURANTS;
import static io.github.jonarzz.kata.unusual.spending.payment.Category.TRAVEL;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jonarzz.kata.unusual.spending.expense.FakeExpenseService;
import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.payment.Category;
import org.junit.jupiter.api.Test;

import java.util.Map;

class UnusualExpensesNotificationServiceTest {

    @Test
    void noUnusualExpenses() {
        var notificationService = createServiceReturning(Map.of());

        var body = notificationService.createNotificationBody();

        assertThat(body)
                .isEmpty();
    }

    @Test
    void singleUnusualExpense() {
        var notificationService = createServiceReturning(Map.of(
                GOLF, usd(123, 85)
        ));

        var body = notificationService.createNotificationBody();

        assertThat(body)
                .hasValue("""
                                  Hello card user!
                                                                     
                                  We have detected unusually high spending on your card in these categories:
                                                                     
                                  * You spent $123.85 on golf
                                                                     
                                  Love,
                                  The Credit Card Company""");
    }

    @Test
    void multipleUnusualExpenses_expensesAreSortedHighToLow() {
        var notificationService = createServiceReturning(Map.of(
                RESTAURANTS, usd(58, 23),
                TRAVEL, usd(990, 99),
                ENTERTAINMENT, usd(257, 13)
        ));

        var body = notificationService.createNotificationBody();

        assertThat(body)
                .hasValue("""
                                  Hello card user!
                                                                     
                                  We have detected unusually high spending on your card in these categories:
                                                                     
                                  * You spent $990.99 on travel
                                  * You spent $257.13 on entertainment
                                  * You spent $58.23 on restaurants
                                                                     
                                  Love,
                                  The Credit Card Company""");
    }


    private UnusualExpensesNotificationService createServiceReturning(Map<Category, Cost> stubbedExpenses) {
        return new UnusualExpensesNotificationService(new FakeExpenseService(stubbedExpenses),
                                                      new UnusualExpenseI18nService(new MessageRegistry()));
    }
}