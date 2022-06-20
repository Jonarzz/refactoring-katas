package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseFetchingAdapter;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseModifyingAdapter;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

class PaymentRepository {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private DatabaseFetchingAdapter queryingAdapter;
    private DatabaseModifyingAdapter modifyingAdapter;

    PaymentRepository() {
        modifyingAdapter = new DatabaseModifyingAdapter();
        queryingAdapter = new DatabaseFetchingAdapter();
    }

    PaymentRepository(String url, String user, String password) {
        modifyingAdapter = new DatabaseModifyingAdapter(url, user, password);
        queryingAdapter = new DatabaseFetchingAdapter(url, user, password);
    }

    Collection<PaymentDetails> getPaymentDetailsBetween(BigInteger payerId, OffsetDateTime from, OffsetDateTime to) {
        var sqlTemplate = "SELECT p.category, p.amount, c.alpha_code, c.language_tag "
                          + "FROM payment p "
                          + "JOIN currency c ON p.currency = c.alpha_code "
                          + "WHERE payer_id = %s "
                          + "AND time >= TIMESTAMP '%s' "
                          + "AND time <= TIMESTAMP '%s'";
        var formattedSql = sqlTemplate.formatted(payerId,
                                                 from.format(DATE_TIME_FORMATTER),
                                                 to.format(DATE_TIME_FORMATTER));
        return queryingAdapter.fetch(formattedSql, new PaymentRepositoryResultMapper());
    }

    public void save(Payment payment) {
        var payerId = payment.payerId();
        if (!queryingAdapter.atLeastOneExists("SELECT 1 FROM payer WHERE id = " + payerId)) {
            throw new IllegalStateException("Payer with ID " + payerId + " does not exist");
        }
        var details = payment.details();
        var cost = details.cost();
        var currency = cost.currency();
        createCurrencyIfDoesNotExist(currency);
        var category = details.category();
        createCategoryIfDoesNotExist(category);
        modifyingAdapter.modify("INSERT INTO payment "
                                + "(id, payer_id, amount, currency, category, time, description) "
                                + "VALUES ('%s', %s, %s, '%s', '%s', '%s', %s)"
                                        .formatted(payment.id(), payerId, cost.amount(), currency.alphaCode(), category,
                                                   payment.timestamp()
                                                          .format(ISO_OFFSET_DATE_TIME),
                                                   details.description()
                                                          .map(description -> "'" + description + "'")
                                                          .orElse(null)));
    }

    private void createCurrencyIfDoesNotExist(Currency currency) {
        var currencyCode = currency.alphaCode();
        var currencyQuery = "SELECT 1 FROM currency WHERE alpha_code = '%s'".formatted(currencyCode);
        if (queryingAdapter.atLeastOneExists(currencyQuery)) {
            return;
        }
        modifyingAdapter.modify("INSERT INTO currency (alpha_code, language_tag) "
                                + "VALUES ('%s', '%s')"
                                        .formatted(currencyCode, currency.languageTag()));
    }

    private void createCategoryIfDoesNotExist(Category category) {
        var categoryQuery = "SELECT 1 FROM category WHERE name = '%s'".formatted(category);
        if (queryingAdapter.atLeastOneExists(categoryQuery)) {
            return;
        }
        modifyingAdapter.modify("INSERT INTO category (name) "
                                + "VALUES ('%s')"
                                        .formatted(category));
    }
}
