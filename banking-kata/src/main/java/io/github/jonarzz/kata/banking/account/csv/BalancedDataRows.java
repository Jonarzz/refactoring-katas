package io.github.jonarzz.kata.banking.account.csv;

import static io.github.jonarzz.kata.banking.account.statement.DataRow.fromOperation;

import io.github.jonarzz.kata.banking.account.statement.DataRow;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class BalancedDataRows {

    private List<DataRow> dataRows = new ArrayList<>();
    private AtomicLong balance = new AtomicLong();

    BalancedDataRows append(TemporalAccessor timestamp, int amount) {
        var newBalance = balance.addAndGet(amount);
        dataRows.add(fromOperation(timestamp, amount, newBalance));
        return this;
    }

    List<DataRow> rows() {
        return dataRows;
    }

    long balance() {
        return balance.get();
    }

    static BalancedDataRows merge(BalancedDataRows first, BalancedDataRows second) {
        var merged = new BalancedDataRows();
        merged.dataRows.addAll(first.dataRows);
        merged.dataRows.addAll(second.dataRows);
        return merged;
    }

}
