package io.github.jonarzz.kata.banking.account.memory;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.github.jonarzz.kata.banking.account.InsufficientFundsException;
import io.github.jonarzz.kata.banking.account.statement.OperationRow;
import io.github.jonarzz.kata.banking.account.statement.Row;
import io.github.jonarzz.kata.banking.account.statement.TableFactory;
import io.github.jonarzz.kata.banking.account.statement.printer.StatementPrinter;
import io.github.jonarzz.kata.banking.account.validation.PositiveAmount;
import io.github.jonarzz.kata.banking.account.validation.ValidatedAccount;

import java.util.ArrayList;
import java.util.List;

class InMemoryAccount<S> extends ValidatedAccount<S> {

    private final TableFactory tableFactory = new TableFactory();
    private final StatementPrinter<S> statementPrinter;
    @GuardedBy("this")
    private final List<AccountOperation> operations = new ArrayList<>();
    @GuardedBy("this")
    private long balance = 0;

    InMemoryAccount(StatementPrinter<S> statementPrinter) {
        this.statementPrinter = statementPrinter;
    }

    @Override
    public void deposit(PositiveAmount positiveAmount) {
        var amount = positiveAmount.value();
        synchronized (this) {
            balance += amount;
            operations.add(AccountOperation.now(amount));
        }
    }

    @Override
    public void withdraw(PositiveAmount positiveAmount) throws InsufficientFundsException {
        var amount = positiveAmount.value();
        synchronized (this) {
            var balanceBefore = balance;
            balance -= amount;
            if (balance < 0) {
                balance = balanceBefore;
                throw new InsufficientFundsException(balance);
            }
            operations.add(AccountOperation.now(-amount));
        }
    }

    @Override
    public S printStatement() {
        List<Row> rows = new ArrayList<>();
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
            rows.add(OperationRow.create(timestamp, amount, accountBalance));
        }
        var table = tableFactory.operationsTableWithHeader(rows);
        return statementPrinter.print(table);
    }

}
