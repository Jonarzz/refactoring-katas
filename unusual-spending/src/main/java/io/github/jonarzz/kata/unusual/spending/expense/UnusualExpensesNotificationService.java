package io.github.jonarzz.kata.unusual.spending.expense;

import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparison.expenses;
import static io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicies.category;
import static io.github.jonarzz.kata.unusual.spending.payment.Timespan.from;

import io.github.jonarzz.kata.unusual.spending.i18n.I18nService;

import java.time.YearMonth;
import java.util.Optional;

public class UnusualExpensesNotificationService {

    private ExpenseService expenseService;
    private I18nService i18nService;

    public UnusualExpensesNotificationService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    Optional<String> createNotificationBody() {
        var currentMonth = YearMonth.now();
        var previousMonth = currentMonth.minusMonths(1);
        var unusualExpenses = expenseService.calculate(expenses(from(currentMonth))
                                                               .groupedBy(category())
                                                               .comparedToExpenses(from(previousMonth))
                                                               .matching(ExpensesIncreaseNoLesserThan.percentage(150)));
        if (unusualExpenses.isEmpty()) {
            return Optional.empty();
        }
        // TODO build message body
        i18nService.getMessage("");
        return Optional.empty();
    }

}
