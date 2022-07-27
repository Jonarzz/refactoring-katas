package io.github.jonarzz.kata.unusual.spending.payment;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.technical.repository.ResultMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class PaymentDetailsMapper implements ResultMapper<PaymentDetails> {

    @Override
    public PaymentDetails map(ResultSet result) throws SQLException {
        var description = result.getString("description");
        var category = Category.named(result.getString("category"));
        var currency = Currency.create(result.getString("alpha_code"),
                                       result.getString("language_tag"));
        var cost = Cost.create(result.getDouble("amount"), currency);
        return new PaymentDetails(category, cost, description);
    }

}
