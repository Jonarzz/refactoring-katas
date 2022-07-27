package io.github.jonarzz.kata.unusual.spending.payment;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class IsolatedTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                // disable Docker testcontainers
                "quarkus.devservices.enabled", "false",
                // disable PaymentRegistrationListener startup
                "quarkus.arc.test.disable-application-lifecycle-observers", "true",
                "quarkus.liquibase.change-log", "test-changeLog.yaml",
                "quarkus.datasource.jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "quarkus.datasource.username", "user",
                "quarkus.datasource.password", "password",
                // required JMS-related properties
                "jms.payment.destination", "dummy",
                "jms.payment.client-id", "dummy",
                "quarkus.artemis.url", "dummy"
        );
    }
}
