package io.github.jonarzz.kata.banking.account.statement.printer.html;

class HtmlTableBuilder {

    private StringBuilder builder = new StringBuilder(Tag.TABLE.start);

    void header(Runnable wrapped) {
        wrap(Tag.THEAD, wrapped);
    }

    void body(Runnable wrapped) {
        wrap(Tag.TBODY, wrapped);
    }

    void row(Runnable wrapped) {
        wrap(Tag.TR, wrapped);
    }

    void headerCell(String value) {
        wrap(Tag.TH, value);
    }

    void bodyCell(String value) {
        wrap(Tag.TD, value);
    }

    String build() {
        return builder.append(Tag.TABLE.end)
                      .toString();
    }

    private void wrap(Tag tag, String value) {
        builder.append(tag.start)
               .append(value)
               .append(tag.end);
    }

    private void wrap(Tag tag, Runnable wrapped) {
        builder.append(tag.start);
        wrapped.run();
        builder.append(tag.end);
    }

    private enum Tag {

        TABLE("table"),
        TR("tr"),
        THEAD("thead"),
        TH("th"),
        TBODY("tbody"),
        TD("td");

        private final String start;
        private final String end;

        Tag(String name) {
            start = "<" + name + ">";
            end = "</" + name + ">";
        }

    }

}
