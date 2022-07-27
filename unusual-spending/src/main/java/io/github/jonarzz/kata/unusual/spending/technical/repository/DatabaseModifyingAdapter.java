package io.github.jonarzz.kata.unusual.spending.technical.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.SQLException;

@ApplicationScoped
public class DatabaseModifyingAdapter {

    private final DataSource dataSource;

    public DatabaseModifyingAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void modify(String sql) {
        // transaction management might be added, but it's not required as of now
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
