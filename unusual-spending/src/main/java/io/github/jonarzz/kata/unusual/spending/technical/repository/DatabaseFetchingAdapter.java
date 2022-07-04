package io.github.jonarzz.kata.unusual.spending.technical.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFetchingAdapter extends DatabaseAdapter {

    public DatabaseFetchingAdapter(String url, String username, String password) {
        super(url, username, password);
    }

    public <T> List<T> fetch(String sql, ResultMapper<T> resultMapper) {
        return handleQuery(sql, resultSet -> {
            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultMapper.map(resultSet));
            }
            return results;
        });
    }

    public boolean atLeastOneExists(String sql) {
        return handleQuery(sql, ResultSet::next);
    }

    private <T> T handleQuery(String sql, ResultSetMapper<T> resultSetMapper) {
        try (var conn = getConnection();
             var statement = conn.prepareStatement(sql)) {
            return resultSetMapper.apply(statement.executeQuery());
        } catch (SQLException e) {
            // TODO improve exception handling in the whole kata
            throw new IllegalStateException(e);
        }
    }

    private interface ResultSetMapper<T> {

        T apply(ResultSet resultSet) throws SQLException;

    }

}
