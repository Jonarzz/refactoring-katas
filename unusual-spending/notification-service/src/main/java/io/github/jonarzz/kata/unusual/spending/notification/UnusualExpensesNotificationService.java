package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.expense.MultiplicationThreshold.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparisonCriteria.forUsername;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.reverseOrder;

import io.github.jonarzz.kata.unusual.spending.expense.ExpenseService;
import io.github.jonarzz.kata.unusual.spending.expense.UnusualExpense;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan;
import org.jboss.logging.Logger;

import java.time.YearMonth;
import java.util.Collection;
import java.util.Optional;

public class UnusualExpensesNotificationService {

    private static final Logger LOG = Logger.getLogger(UnusualExpensesNotificationService.class);

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

    public Optional<String> createNotificationBody(String username) {
        LOG.debugf("Creating notification body for user %s", username);
        var currentMonth = YearMonth.now();
        var previousMonth = currentMonth.minusMonths(1);
        var unusualExpenses = expenseService.calculateUnusualExpenses(forUsername(username)
                                                                              .aggregateExpenses(AggregationTimespan.of(currentMonth))
                                                                              .groupedBy(AggregationPolicy.category())
                                                                              .comparedToAggregatedExpenses(AggregationTimespan.of(previousMonth))
                                                                              .increasedByAtLeast(percentage(150)));
        if (unusualExpenses.isEmpty()) {
            LOG.debugf("Unusual expenses not found for user %s", username);
            return Optional.empty();
        }
        LOG.debugf("Found %s unusual expenses for user %s - building notification message",
                   unusualExpenses.size(), username);
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
