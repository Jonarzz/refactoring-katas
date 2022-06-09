package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.expense.ThresholdValue.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparison.forUserId;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy.category;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan.fromWhole;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.reverseOrder;

import io.github.jonarzz.kata.unusual.spending.expense.ExpenseService;
import io.github.jonarzz.kata.unusual.spending.expense.UnusualExpense;

import java.math.BigInteger;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Optional;

public class UnusualExpensesNotificationService {

    private ExpenseService expenseService;
    private CommonI18nService commonI18nService;
    private UnusualExpenseI18nService unusualExpenseI18nService;

    public UnusualExpensesNotificationService(ExpenseService expenseService,
                                              CommonI18nService commonI18nService,
                                              UnusualExpenseI18nService unusualExpenseI18nService) {
        this.expenseService = expenseService;
        this.commonI18nService = commonI18nService;
        this.unusualExpenseI18nService = unusualExpenseI18nService;
    }

    // TODO web api (REST)
    // TODO mail sending package + web api displaying notifications sent (GraphQL)
    // TODO user package + persistence + web api (GraphQL)
    // TODO divide into modules -> microservices
    // TODO triggered as a scheduled task (external service)
    // TODO Docker/Kubernetes
    // TODO logging + elastic
    // TODO Prometheus
    // TODO front-end
    // TODO Cypress tests

    public Optional<String> createNotificationBody(BigInteger userId) {
        var currentMonth = YearMonth.now();
        var previousMonth = currentMonth.minusMonths(1);
        var unusualExpenses = expenseService.calculate(forUserId(userId)
                                                               .aggregateExpenses(fromWhole(currentMonth))
                                                               .groupedBy(category())
                                                               .comparedToAggregatedExpenses(fromWhole(previousMonth))
                                                               .increasedByAtLeast(percentage(150)));
        if (unusualExpenses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(buildNotification(unusualExpenses));
    }

    private String buildNotification(Collection<UnusualExpense> unusualExpenses) {
        var bodyBuilder = new StringBuilder(unusualExpenseI18nService.getMessageBeginningGreeting())
                .append(lineSeparator())
                .append(lineSeparator())
                .append(unusualExpenseI18nService.getExpenseLinesPrefix())
                .append(lineSeparator())
                .append(lineSeparator());
        unusualExpenses.stream()
                       // high to low expenses
                       .sorted(reverseOrder())
                       .map(expense -> unusualExpenseI18nService.getUnusualExpenseLine(expense.category(),
                                                                                       expense.amount()))
                       .forEach(line -> bodyBuilder.append(line)
                                                   .append(lineSeparator()));
        return bodyBuilder.append(lineSeparator())
                          .append(unusualExpenseI18nService.getMessageEndingGreeting())
                          .append(lineSeparator())
                          .append(commonI18nService.getCompanyName())
                          .toString();
    }

}
