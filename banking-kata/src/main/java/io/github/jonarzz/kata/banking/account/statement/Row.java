package io.github.jonarzz.kata.banking.account.statement;

public interface Row {

    String[] cells();

    default boolean isHeader() {
        return false;
    }

}
