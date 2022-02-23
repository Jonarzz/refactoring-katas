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
    public synchronized void deposit(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposition amount cannot be negative");
        }
        balance += amount;
        operations.add(AccountOperation.now(amount));
    }

    @Override
    public synchronized void withdraw(int amount) throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        }
        var balanceBefore = balance;
        balance -= amount;
        if (balance < 0) {
            balance = balanceBefore;
            throw new InsufficientFundsException();
        }
        operations.add(AccountOperation.now(-amount));
    }

    @Override
    public String printStatement() {
        List<DataRow> rows = new ArrayList<>();
        synchronized (this) {
            long accountBalance = 0;
            for (var operation : operations) {
                var timestamp = operation.timestamp();
                var amount = operation.amount();
                accountBalance += amount;
                rows.add(new DataRow(timestamp, amount, accountBalance));
            }
        }
        return Statement.withHeader(rows)
                        .toString();
    }

}
