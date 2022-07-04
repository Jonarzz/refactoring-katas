package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseFetchingAdapter;
import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseModifyingAdapter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
class PaymentRepository {

    private static final Logger LOG = Logger.getLogger(PaymentRepository.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private DatabaseFetchingAdapter queryingAdapter;
    private DatabaseModifyingAdapter modifyingAdapter;

    PaymentRepository(@ConfigProperty(name = "database.payment.url") String url,
                      @ConfigProperty(name = "database.payment.username") String user,
                      // ugly H2 workaround to save time - default 'sa' user password is empty; Quarkus ignores an empty default value
                      @ConfigProperty(name = "database.payment.password") Optional<String> password) {
        var passwordValue = password.orElse("");
        LOG.infof("Creating database adapters for URL '%s' and user '%s", url, user);
        modifyingAdapter = new DatabaseModifyingAdapter(url, user, passwordValue);
        queryingAdapter = new DatabaseFetchingAdapter(url, user, passwordValue);
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

    public void save(PaymentRegisteredEvent paymentEvent) {
        var payerId = paymentEvent.payerId();
        if (!queryingAdapter.atLeastOneExists("SELECT 1 FROM payer WHERE id = " + payerId)) {
            throw new IllegalStateException("Payer with ID " + payerId + " does not exist");
        }
        var details = paymentEvent.details();
        var cost = details.cost();
        var currency = cost.currency();
        createCurrencyIfDoesNotExist(currency);
        var category = details.category();
        createCategoryIfDoesNotExist(category);
        modifyingAdapter.modify("INSERT INTO payment "
                                + "(id, payer_id, amount, currency, category, time, description) "
                                + "VALUES ('%s', %s, %s, '%s', '%s', '%s', %s)"
                                        .formatted(paymentEvent.id(), payerId, cost.amount(), currency.alphaCode(), category,
                                                   paymentEvent.timestamp()
                                                               .format(ISO_OFFSET_DATE_TIME),
                                                   Optional.ofNullable(details.description())
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
