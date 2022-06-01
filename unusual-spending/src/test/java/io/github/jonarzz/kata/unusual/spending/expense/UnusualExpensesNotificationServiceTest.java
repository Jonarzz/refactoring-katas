package io.github.jonarzz.kata.unusual.spending.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class UnusualExpensesNotificationServiceTest {

    ExpenseService expenseService = mock(ExpenseService.class);
    UnusualExpensesNotificationService notificationService = new UnusualExpensesNotificationService(expenseService);

    @BeforeEach
    void setUp() {
        when(expenseService.calculate(any()))
                .thenReturn(Set.of());
    }

    @Test
    void noExpensesWith150PercentIncreaseBetweenMonthsInAnyCategory() {
        var body = notificationService.createNotificationBody();

        assertThat(body)
                .isEmpty();
    }

    // TODO other tests

}