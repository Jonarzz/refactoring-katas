package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.technical.repository.DatabaseFetchingAdapter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

class PaymentRepository {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DatabaseFetchingAdapter fetchingAdapter = new DatabaseFetchingAdapter();

    // TODO payments saving

    Collection<Payment> getUserPaymentsBetween(BigInteger userId, LocalDateTime from, LocalDateTime to) {
        return fetchingAdapter.query(prepareSql(userId, from, to),
                                     new PaymentMapper());
    }

    private String prepareSql(BigInteger userId, LocalDateTime from, LocalDateTime to) {
        var sqlTemplate = "SELECT category, currency, amount FROM payment "
                          + "WHERE payer_id = %s "
                          + "AND time >= TIMESTAMP '%s' "
                          + "AND time <= TIMESTAMP '%s'";
        return sqlTemplate.formatted(userId,
                                     from.format(DATE_TIME_FORMATTER),
                                     to.format(DATE_TIME_FORMATTER));
    }
}
