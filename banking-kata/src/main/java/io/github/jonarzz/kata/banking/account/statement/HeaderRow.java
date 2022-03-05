package io.github.jonarzz.kata.banking.account.statement;

class HeaderRow extends BaseRow {

    HeaderRow(String... cells) {
        super(cells);
    }

    @Override
    public boolean isHeader() {
        return true;
    }

}
