package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseFetchingAdapter;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseModifyingAdapter;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
class PaymentRepository {

    private DatabaseFetchingAdapter queryingAdapter;
    private DatabaseModifyingAdapter modifyingAdapter;

    PaymentRepository(DatabaseFetchingAdapter queryingAdapter,
                      DatabaseModifyingAdapter modifyingAdapter) {
        this.queryingAdapter = queryingAdapter;
        this.modifyingAdapter = modifyingAdapter;
    }

    Collection<PaymentDetails> getPaymentDetailsBetween(Long payerId, OffsetDateTime from, OffsetDateTime to) {
        var sqlBuilder = new StringBuilder("SELECT p.category, p.amount, c.alpha_code, c.language_tag, p.description ")
                .append("FROM payment p ")
                .append("JOIN currency c ON p.currency = c.alpha_code ")
                .append("WHERE payer_id = ")
                .append(payerId);
        if (from != null) {
            sqlBuilder.append(" AND time >= TIMESTAMP '")
                      .append(toStringAtUtc(from))
                      .append("'");
        }
        if (to != null) {
            sqlBuilder.append(" AND time <= TIMESTAMP '")
                      .append(toStringAtUtc(to))
                      .append("'");
        }
        return queryingAdapter.fetch(sqlBuilder.toString(), new PaymentDetailsMapper());
    }

    boolean save(PaymentRegisteredEvent paymentEvent) {
        var payerId = paymentEvent.payerId();
        if (!queryingAdapter.atLeastOneExists("SELECT 1 FROM payer WHERE id = " + payerId)) {
            throw new IllegalStateException("Payer with ID " + payerId + " does not exist");
        }
        var eventId = paymentEvent.id();
        if (queryingAdapter.atLeastOneExists("SELECT 1 FROM payment WHERE id = '" + eventId + "'")) {
            return false;
        }
        var details = paymentEvent.details();
        var cost = details.cost();
        var currency = cost.getCurrency();
        createCurrencyIfDoesNotExist(currency);
        var category = details.category();
        createCategoryIfDoesNotExist(category);
        var time = toStringAtUtc(Optional.ofNullable(paymentEvent.timestamp())
                                          .orElseGet(OffsetDateTime::now));
        // TODO pass sql and parameters and use in PreparedStatement in the adapters (whole class)
        modifyingAdapter.modify("INSERT INTO payment "
                                + "(id, payer_id, amount, currency, category, time, description) "
                                + "VALUES ('%s', %s, %s, '%s', '%s', '%s', %s)"
                                        .formatted(eventId, payerId, cost.getAmount(), currency.alphaCode(), category, time,
                                                   Optional.ofNullable(details.description())
                                                           .map(description -> "'" + description + "'")
                                                           .orElse(null)));
        return true;
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

    private String toStringAtUtc(OffsetDateTime from) {
        return from.atZoneSameInstant(UTC)
                   .format(ISO_OFFSET_DATE_TIME);
    }
}
