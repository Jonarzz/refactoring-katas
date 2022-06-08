package io.github.jonarzz.kata.unusual.spending.technical.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class DatabaseAdapter {

    private final String url;
    private final String username;
    private final String password;

    DatabaseAdapter(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    DatabaseAdapter() {
        this(System.getProperty("url"),
             System.getProperty("username"),
             System.getProperty("password"));
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
