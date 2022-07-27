package io.github.jonarzz.kata.unusual.spending.technical.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class DatabaseModifyingAdapter {

    private final DataSource dataSource;

    public DatabaseModifyingAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void modify(String sqlTemplate, Object... params) {
        // transaction management might be added, but it's not required as of now
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sqlTemplate)) {
            for (int index = 0; index < params.length; index++) {
                var param = Optional.ofNullable(params[index])
                                    .map(Objects::toString)
                                    .orElse(null);
                statement.setString(index + 1, param);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
