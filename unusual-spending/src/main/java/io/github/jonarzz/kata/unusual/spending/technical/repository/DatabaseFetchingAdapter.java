package io.github.jonarzz.kata.unusual.spending.technical.repository;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFetchingAdapter {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseFetchingAdapter(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public DatabaseFetchingAdapter() {
        this(System.getProperty("url"),
             System.getProperty("username"),
             System.getProperty("password"));
    }

    public <T> List<T> query(String sql, ResultMapper<T> resultMapper) {
        List<T> results = new ArrayList<>();
        try (var conn = DriverManager.getConnection(url, username, password);
             var statement = conn.createStatement();
             var resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                results.add(resultMapper.map(resultSet));
            }
        } catch (SQLException e) {
            // TODO improve exception handling in the whole kata
            throw new IllegalStateException(e);
        }
        return results;
    }

}
