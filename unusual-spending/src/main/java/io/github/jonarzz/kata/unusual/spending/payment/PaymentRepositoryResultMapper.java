package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.ResultMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class PaymentRepositoryResultMapper implements ResultMapper<Payment> {

    @Override
    public Payment map(ResultSet result) throws SQLException {
        var category = Category.named(result.getString("category"));
        var amount = result.getDouble("amount");
        var currency = Currency.create(result.getString("alpha_code"),
                                       result.getString("language_tag"));
        return new Payment(category, Cost.create(amount, currency));
    }

}
