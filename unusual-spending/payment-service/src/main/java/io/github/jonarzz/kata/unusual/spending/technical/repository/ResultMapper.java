package io.github.jonarzz.kata.unusual.spending.technical.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultMapper<T> {

    T map(ResultSet result) throws SQLException;

}
