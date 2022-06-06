package io.github.jonarzz.kata.unusual.spending.payment;

import static io.github.jonarzz.kata.unusual.spending.money.Currency.USD;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.technical.repository.ResultMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class PaymentMapper implements ResultMapper<Payment> {

    @Override
    public Payment map(ResultSet result) throws SQLException {
        return new Payment(Category.named(result.getString("category")),
                           Cost.create(result.getDouble("amount"),
                                       USD)); // TODO currency
    }

}
