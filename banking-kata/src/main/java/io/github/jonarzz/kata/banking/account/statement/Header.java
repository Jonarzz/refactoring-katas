package io.github.jonarzz.kata.banking.account.statement;

class Header extends Row {

    static final String DATE_HEADER_NAME = "Date";
    static final String AMOUNT_HEADER_NAME = "Amount";
    static final String BALANCE_HEADER_NAME = "Balance";

    Header() {
        super(DATE_HEADER_NAME, AMOUNT_HEADER_NAME, BALANCE_HEADER_NAME);
    }

}
