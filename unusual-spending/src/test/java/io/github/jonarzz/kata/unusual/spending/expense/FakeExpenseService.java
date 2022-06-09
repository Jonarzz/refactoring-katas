package io.github.jonarzz.kata.unusual.spending.expense;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.payment.Category;

import java.util.Collection;
import java.util.Map;

public class FakeExpenseService extends ExpenseService {

    private Map<Category, Cost> totalExpensesByCategory;

    public FakeExpenseService(Map<Category, Cost> totalExpensesByCategory) {
        super(null);
        this.totalExpensesByCategory = totalExpensesByCategory;
    }

    @Override
    public Collection<UnusualExpense> calculate(TimestampedExpenseComparison expenseComparison) {
        return totalExpensesByCategory.entrySet()
                                      .stream()
                                      .map((entry) -> new UnusualExpense(entry.getKey(), entry.getValue()))
                                      .toList();
    }

}
