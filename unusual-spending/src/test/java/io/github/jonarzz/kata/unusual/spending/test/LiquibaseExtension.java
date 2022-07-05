package io.github.jonarzz.kata.unusual.spending.test;

import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.DriverManager;
import java.util.TimeZone;

public class LiquibaseExtension implements BeforeAllCallback {

    public static final String URL = System.getProperty("liquibase.url");
    public static final String USERNAME = System.getProperty("liquibase.username");
    public static final String PASSWORD = System.getProperty("liquibase.password");

    private static final String CHANGE_LOG_FILE = "changelog.yaml";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        var initialTimeZone = TimeZone.getDefault();
        // simulate data being added using the repositories (timestamps converted to UTC)
        TimeZone.setDefault(getTimeZone(UTC));
        var connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        var database = DatabaseFactory.getInstance()
                                      .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        var liquibase = new Liquibase(CHANGE_LOG_FILE, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts());
        TimeZone.setDefault(initialTimeZone);
    }

}
