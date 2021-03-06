package io.github.jonarzz.kata.banking.account.csv;

import static io.github.jonarzz.kata.banking.account.statement.OperationRow.create;

import io.github.jonarzz.kata.banking.account.statement.Row;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

class BalancedDataRows {

    private List<Row> dataRows = new ArrayList<>();
    private AtomicLong balance = new AtomicLong();

    BalancedDataRows append(TemporalAccessor timestamp, int amount) {
        var newBalance = balance.addAndGet(amount);
        dataRows.add(create(timestamp, amount, newBalance));
        return this;
    }

    List<Row> rows() {
        return List.copyOf(dataRows);
    }

    long balance() {
        return balance.get();
    }

    static BalancedDataRows merge(BalancedDataRows first, BalancedDataRows second) {
        var merged = new BalancedDataRows();
        Stream.of(first, second)
              .forEach(element -> {
                  merged.dataRows.addAll(element.dataRows);
                  merged.balance.addAndGet(element.balance());
              });
        return merged;
    }

}
