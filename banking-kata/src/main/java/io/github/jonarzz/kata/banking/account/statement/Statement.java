package io.github.jonarzz.kata.banking.account.statement;

import static io.github.jonarzz.kata.banking.account.statement.DataRow.MAX_TIMESTAMP_LENGTH;
import static io.github.jonarzz.kata.banking.account.statement.Header.AMOUNT_HEADER_NAME;
import static io.github.jonarzz.kata.banking.account.statement.Header.BALANCE_HEADER_NAME;

import java.util.List;
import java.util.StringJoiner;

public record Statement(List<DataRow> rows) {

    @Override
    public String toString() {
        int maxAmountLength = AMOUNT_HEADER_NAME.length();
        int maxBalanceLength = BALANCE_HEADER_NAME.length();
        for (var row : rows) {
            maxAmountLength = Math.max(row.amount().length(), maxAmountLength);
            maxBalanceLength = Math.max(row.balance().length(), maxBalanceLength);
        }
        var rowJoiner = new StringJoiner("\n");
        for (var row : rows) {
            rowJoiner.add(row.formatted(MAX_TIMESTAMP_LENGTH, maxAmountLength, maxBalanceLength));
        }
        return new Header().formatted(MAX_TIMESTAMP_LENGTH, maxAmountLength, maxBalanceLength)
               + "\n"
               + rowJoiner;
    }

}
