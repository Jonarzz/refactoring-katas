package io.github.jonarzz.kata.unusual.spending.technical.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DatabaseFetchingAdapter {

    private final DataSource dataSource;

    public DatabaseFetchingAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> fetch(ResultMapper<T> resultMapper, String sqlTemplate, Object... params) {
        return handleQuery(resultSet -> {
            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultMapper.map(resultSet));
            }
            return results;
        }, sqlTemplate, params);
    }

    public <T> Optional<T> fetchSingle(ResultMapper<T> resultMapper, String sqlTemplate, Object... params) {
        return handleQuery(resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultMapper.map(resultSet));
            }
            return Optional.empty();
        }, sqlTemplate, params);
    }

    public boolean atLeastOneExists(String sql, Object... params) {
        return handleQuery(ResultSet::next, sql, params);
    }

    private <T> T handleQuery(ResultSetMapper<T> resultSetMapper, String sql, Object... params) {
        try (var conn = dataSource.getConnection();
             var statement = conn.prepareStatement(sql)) {
            var templateIndex = 1;
            for (var param : params) {
                if (param != null) {
                    statement.setString(templateIndex++, param.toString());
                }
            }
            return resultSetMapper.apply(statement.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private interface ResultSetMapper<T> {

        T apply(ResultSet resultSet) throws SQLException;

    }

}
