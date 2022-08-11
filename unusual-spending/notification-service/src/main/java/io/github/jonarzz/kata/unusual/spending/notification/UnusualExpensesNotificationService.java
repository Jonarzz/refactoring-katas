package io.github.jonarzz.kata.unusual.spending.notification;

import static io.github.jonarzz.kata.unusual.spending.expense.MultiplicationThreshold.percentage;
import static io.github.jonarzz.kata.unusual.spending.expense.TimestampedExpenseComparisonCriteria.forUsername;
import static io.github.jonarzz.kata.unusual.spending.payment.AggregationTimespan.of;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.reverseOrder;

import io.github.jonarzz.kata.unusual.spending.expense.ExpenseService;
import io.github.jonarzz.kata.unusual.spending.expense.UnusualExpense;
import io.github.jonarzz.kata.unusual.spending.payment.AggregationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class UnusualExpensesNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnusualExpensesNotificationService.class);

    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

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
        LOGGER.debug("Creating notification body for user {}", username);
        var currentMonth = YearMonth.now();
        var previousMonth = currentMonth.minusMonths(1);
        var unusualExpenses = expenseService.calculateUnusualExpenses(forUsername(username)
                                                                              .aggregateExpenses(of(currentMonth))
                                                                              .groupedBy(AggregationPolicy.category())
                                                                              .comparedToAggregatedExpenses(of(previousMonth))
                                                                              .increasedByAtLeast(percentage(150)));
        if (unusualExpenses.isEmpty()) {
            LOGGER.debug("Unusual expenses not found for user {}", username);
            return Optional.empty();
        }
        LOGGER.debug("Found {} unusual expenses for user {} - building notification message",
                     unusualExpenses.size(), username);
        return Optional.of(buildNotification(unusualExpenses));
    }

    private String buildNotification(Collection<UnusualExpense> unusualExpenses) {
        var bodyBuilder = new StringBuilder(unusualExpenseI18nService.getMessageBeginningGreeting(DEFAULT_LOCALE))
                .append(lineSeparator())
                .append(lineSeparator())
                .append(unusualExpenseI18nService.getExpenseLinesPrefix(DEFAULT_LOCALE))
                .append(lineSeparator())
                .append(lineSeparator());
        unusualExpenses.stream()
                       // high to low expenses
                       .sorted(reverseOrder())
                       .map(expense -> unusualExpenseI18nService.getUnusualExpenseLine(DEFAULT_LOCALE,
                                                                                       expense.category(),
                                                                                       expense.amount()))
                       .forEach(line -> bodyBuilder.append(line)
                                                   .append(lineSeparator()));
        return bodyBuilder.append(lineSeparator())
                          .append(unusualExpenseI18nService.getMessageEndingGreeting(DEFAULT_LOCALE))
                          .append(lineSeparator())
                          .append(commonI18nService.getCompanyName(DEFAULT_LOCALE))
                          .toString();
    }

}
