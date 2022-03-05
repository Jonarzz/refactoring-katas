package io.github.jonarzz.kata.banking.account.statement;

abstract class BaseRow implements Row {

    private String[] cells;

    BaseRow(String... cells) {
        this.cells = cells;
    }

    @Override
    public final String[] cells() {
        return cells;
    }

}
