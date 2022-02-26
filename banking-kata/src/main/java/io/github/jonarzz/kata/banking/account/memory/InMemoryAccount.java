package io.github.jonarzz.kata.banking.account.memory;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.github.jonarzz.kata.banking.account.Account;
import io.github.jonarzz.kata.banking.account.InsufficientFundsException;
import io.github.jonarzz.kata.banking.account.statement.DataRow;
import io.github.jonarzz.kata.banking.account.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAccount implements Account {

    @GuardedBy("this")
    private final List<AccountOperation> operations = new ArrayList<>();

    @GuardedBy("this")
    private long balance = 0;

    @Override
    public void deposit(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposition amount cannot be negative");
        }
        synchronized (this) {
            balance += amount;
            operations.add(AccountOperation.now(amount));
        }
    }

    @Override
    public void withdraw(int amount) throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        }
        synchronized (this) {
            var balanceBefore = balance;
            balance -= amount;
            if (balance < 0) {
                balance = balanceBefore;
                throw new InsufficientFundsException();
            }
            operations.add(AccountOperation.now(-amount));
        }
    }

    @Override
    public String printStatement() {
        List<DataRow> rows = new ArrayList<>();
        int operationsCountSnapshot;
        synchronized (this) {
            // operations list is add-only
            operationsCountSnapshot = operations.size();
        }
        long accountBalance = 0;
        for (int i = 0; i < operationsCountSnapshot; i++) {
            var operation = operations.get(i);
            var timestamp = operation.timestamp();
            var amount = operation.amount();
            accountBalance += amount;
            rows.add(new DataRow(timestamp, amount, accountBalance));
        }
        return Statement.withHeader(rows)
                        .toString();
    }

}
