package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.LocalTime.MAX;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseFetchingAdapter;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseModifyingAdapter;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
class PaymentRepository {

    private DatabaseFetchingAdapter queryingAdapter;
    private DatabaseModifyingAdapter modifyingAdapter;

    PaymentRepository(DatabaseFetchingAdapter queryingAdapter,
                      DatabaseModifyingAdapter modifyingAdapter) {
        this.queryingAdapter = queryingAdapter;
        this.modifyingAdapter = modifyingAdapter;
    }

    Collection<PaymentDetails> getPaymentDetailsBetween(String payerUsername, LocalDate from, LocalDate to) {
        return getPaymentDetailsBetween(payerUsername,
                                        toUtcDateTime(from, LocalDate::atStartOfDay),
                                        toUtcDateTime(to, date -> date.atTime(MAX)));
    }

    Collection<PaymentDetails> getPaymentDetailsBetween(String payerUsername, OffsetDateTime from, OffsetDateTime to) {
        var sqlTemplate = "SELECT p.id, p.category, p.amount, c.alpha_code, c.language_tag, p.description, p.time "
                         + "FROM payment p "
                         + "JOIN currency c ON p.currency = c.alpha_code "
                         + "WHERE p.payer_username = ?";
        if (from != null) {
            sqlTemplate += " AND p.time >= ?";
        }
        if (to != null) {
            sqlTemplate += " AND p.time <= ?";
        }
        return queryingAdapter.fetch(new PaymentDetailsMapper(),
                                     sqlTemplate, payerUsername, toStringAtUtc(from), toStringAtUtc(to));
    }

    Optional<PaymentDetails> getPaymentDetails(UUID paymentId) {
        return queryingAdapter.fetchSingle(
                new PaymentDetailsMapper(),
                "SELECT p.id, p.category, p.amount, c.alpha_code, c.language_tag, p.description, p.time "
                + "FROM payment p "
                + "JOIN currency c ON p.currency = c.alpha_code "
                + "WHERE p.id = ?",
                paymentId);
    }

    boolean save(PaymentRegisteredEvent paymentEvent) {
        var payerUsername = paymentEvent.payerUsername();
        var details = paymentEvent.details();
        var eventId = details.id();
        if (queryingAdapter.atLeastOneExists("SELECT 1 FROM payment WHERE id = ?", eventId)) {
            return false;
        }
        var cost = details.cost();
        var currency = cost.getCurrency();
        createCurrencyIfDoesNotExist(currency);
        var category = details.category();
        createCategoryIfDoesNotExist(category);
        var time = toStringAtUtc(Optional.ofNullable(details.timestamp())
                                         .orElseGet(OffsetDateTime::now));
        modifyingAdapter.modify("INSERT INTO payment "
                                + "(id, payer_username, amount, currency, category, time, description) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                                eventId, payerUsername, cost.getAmount(), currency.alphaCode(), category, time, details.description());
        return true;
    }

    private void createCurrencyIfDoesNotExist(Currency currency) {
        var currencyCode = currency.alphaCode();
        if (queryingAdapter.atLeastOneExists("SELECT 1 FROM currency WHERE alpha_code = ?", currencyCode)) {
            return;
        }
        modifyingAdapter.modify("INSERT INTO currency (alpha_code, language_tag) VALUES (?, ?)",
                                currencyCode, currency.languageTag());
    }

    private void createCategoryIfDoesNotExist(Category category) {
        if (queryingAdapter.atLeastOneExists("SELECT 1 FROM category WHERE name = ?", category)) {
            return;
        }
        modifyingAdapter.modify("INSERT INTO category (name) VALUES (?)",
                                category);
    }

    private OffsetDateTime toUtcDateTime(LocalDate date, Function<LocalDate, LocalDateTime> timeFunction) {
        return Optional.ofNullable(date)
                       .map(timeFunction)
                       .map(dateTime -> dateTime.atOffset(UTC))
                       .orElse(null);
    }

    private String toStringAtUtc(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withOffsetSameInstant(UTC)
                       .format(ISO_LOCAL_DATE_TIME);
    }
}
