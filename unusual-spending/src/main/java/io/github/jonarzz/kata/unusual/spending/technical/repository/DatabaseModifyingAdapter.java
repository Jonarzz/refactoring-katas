package io.github.jonarzz.kata.unusual.spending.technical.repository;

import java.sql.SQLException;

public class DatabaseModifyingAdapter extends DatabaseAdapter {

    public DatabaseModifyingAdapter() {
    }

    public DatabaseModifyingAdapter(String url, String username, String password) {
        super(url, username, password);
    }

    public void modify(String sql) {
        // transaction management might be added, but it's not required as of now
        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
