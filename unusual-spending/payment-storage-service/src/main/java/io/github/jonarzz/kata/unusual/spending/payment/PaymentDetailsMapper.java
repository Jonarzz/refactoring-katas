package io.github.jonarzz.kata.unusual.spending.payment;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.ResultMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;
import java.util.UUID;

class PaymentDetailsMapper implements ResultMapper<PaymentDetails> {

    private static final DateTimeFormatter DB_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    @Override
    public PaymentDetails map(ResultSet result) throws SQLException {
        var id = UUID.fromString(result.getString("id"));
        var category = Category.named(result.getString("category"));
        var currency = Currency.create(result.getString("alpha_code"),
                                       result.getString("language_tag"));
        var cost = Cost.create(result.getDouble("amount"), currency);
        var timestamp = Optional.ofNullable(result.getString("time"))
                                .map(value -> LocalDateTime.parse(value, DB_DATE_TIME_FORMAT))
                                .map(dateTime -> OffsetDateTime.of(dateTime, UTC))
                                .orElse(null);
        var description = result.getString("description");
        return new PaymentDetails(id, category, cost, timestamp, description);
    }

}
