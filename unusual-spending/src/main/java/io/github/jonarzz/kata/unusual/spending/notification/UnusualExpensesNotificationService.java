package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdValue.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparison.WithThreshold.expenses;
import static io.github.jonarzz.kata.unusual.spending.payment.GroupingPolicies.category;
import static io.github.jonarzz.kata.unusual.spending.payment.Timespan.from;
import static java.util.Comparator.reverseOrder;

import io.github.jonarzz.kata.unusual.spending.expense.ExpenseService;

import java.time.YearMonth;
import java.util.Optional;

public class UnusualExpensesNotificationService {

    private ExpenseService expenseService;
    private UnusualExpenseI18nService i18nService;

    public UnusualExpensesNotificationService(ExpenseService expenseService, UnusualExpenseI18nService i18nService) {
        this.expenseService = expenseService;
        this.i18nService = i18nService;
    }

    public Optional<String> createNotificationBody() { // TODO retrieval for given user ID
        var currentMonth = YearMonth.now();
        var previousMonth = currentMonth.minusMonths(1);
        var unusualExpenses = expenseService.calculate(expenses(from(currentMonth))
                                                               .groupedBy(category())
                                                               .comparedToExpenses(from(previousMonth))
                                                               .increasedByAtLeast(percentage(150)));
        if (unusualExpenses.isEmpty()) {
            return Optional.empty();
        }
        var bodyBuilder = new StringBuilder(i18nService.getMessageBeginning());
        unusualExpenses.stream()
                       // high to low expenses
                       .sorted(reverseOrder())
                       .map(expense -> i18nService.getUnusualExpenseLine(expense.category(),
                                                                         expense.amount()))
                       .forEach(bodyBuilder::append);
        bodyBuilder.append(i18nService.getMessageEnding());
        return Optional.of(bodyBuilder.toString());
    }

}
