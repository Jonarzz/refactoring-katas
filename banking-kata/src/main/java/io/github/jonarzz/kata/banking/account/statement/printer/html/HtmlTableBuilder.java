package io.github.jonarzz.kata.banking.account.statement.printer.html;

class HtmlTableBuilder {

    private StringBuilder builder = new StringBuilder(Tags.TABLE.start);

    void header(Runnable wrapped) {
        wrap(Tags.THEAD, wrapped);
    }

    void body(Runnable wrapped) {
        wrap(Tags.TBODY, wrapped);
    }

    void row(Runnable wrapped) {
        wrap(Tags.TR, wrapped);
    }

    void headerCell(String value) {
        wrap(Tags.TH, value);
    }

    void bodyCell(String value) {
        wrap(Tags.TD, value);
    }

    String build() {
        return builder.append(Tags.TABLE.end)
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

    private static class Tags {

        private static final Tag TABLE = new Tag("table");
        private static final Tag TR = new Tag("tr");
        private static final Tag THEAD = new Tag("thead");
        private static final Tag TH = new Tag("th");
        private static final Tag TBODY = new Tag("tbody");
        private static final Tag TD = new Tag("td");

    }

    private static class Tag {

        private final String start;
        private final String end;

        private Tag(String name) {
            start = "<" + name + ">";
            end = "</" + name + ">";
        }

    }

}
