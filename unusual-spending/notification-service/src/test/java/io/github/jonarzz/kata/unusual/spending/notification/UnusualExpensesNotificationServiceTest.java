package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.money.Cost.usd;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jonarzz.kata.unusual.spending.expense.FakeExpenseService;
import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.payment.Category;
import org.junit.jupiter.api.Test;

import java.util.Map;

class UnusualExpensesNotificationServiceTest {

    static final MessageRegistry MESSAGE_REGISTRY = new MessageRegistry();

    @Test
    void noUnusualExpenses() {
        var notificationService = createServiceReturning(Map.of());

        var body = notificationService.createNotificationBody("user_1");

        assertThat(body)
                .isEmpty();
    }

    @Test
    void singleUnusualExpense() {
        var notificationService = createServiceReturning(Map.of(
                Category.named("GOLF"), usd(123, 85)
        ));

        var body = notificationService.createNotificationBody("user_1");

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
                Category.named("RESTAURANTS"), usd(58, 23),
                Category.named("traVEL"), usd(990, 99),
                Category.named("enTERTAINment"), usd(257, 13)
        ));

        var body = notificationService.createNotificationBody("user_1");

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
                                                      new CommonI18nService(MESSAGE_REGISTRY),
                                                      new UnusualExpenseI18nService(MESSAGE_REGISTRY));
    }
}