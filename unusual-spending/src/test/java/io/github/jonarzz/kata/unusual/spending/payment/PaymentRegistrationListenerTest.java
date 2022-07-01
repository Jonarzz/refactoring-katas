package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_ROLE_FILE;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_USER_FILE;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@QuarkusTest
@TestProfile(PaymentRegistrationListenerTest.Profile.class)
class PaymentRegistrationListenerTest {

    static final String ARTEMIS_URL = "vm://localhost:61616";
    // see: broker.xml resource and properties files in the jms/ subdirectory
    static final String PRODUCER_USERNAME = "producer";
    static final String PRODUCER_PASSWORD = "producer123";
    static final String CONSUMER_USERNAME = "consumer";
    static final String CONSUMER_PASSWORD = "consumer321";

    static final EmbeddedActiveMQ EMBEDDED_ACTIVE_MQ = new EmbeddedActiveMQ();

    static final int SINGLE_RUN_MESSAGE_COUNT = 10;

    static CountDownLatch messagesLatch = new CountDownLatch(SINGLE_RUN_MESSAGE_COUNT);

    @BeforeEach
    void setUp() {
        messagesLatch = new CountDownLatch(SINGLE_RUN_MESSAGE_COUNT);
        try (var connectionFactory = new ActiveMQConnectionFactory(ARTEMIS_URL, PRODUCER_USERNAME, PRODUCER_PASSWORD);
             var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE)) {
            var paymentQueue = context.createQueue("paymentQueue");
            var producer = context.createProducer();
            for (int i = 0; i < SINGLE_RUN_MESSAGE_COUNT; i++) {
                producer.send(paymentQueue, "test message " + i);
            }
        }
    }

    @RepeatedTest(5)
    void messagesAreConsumed() throws InterruptedException {
        assertThat(messagesLatch.await(1, SECONDS))
                .as("Latch counting %d messages stopped at %d",
                    SINGLE_RUN_MESSAGE_COUNT, messagesLatch.getCount())
                .isTrue();
    }

    @ApplicationScoped
    static class JmsServerInitializer {

        void onStart(@Observes StartupEvent event) throws Exception {
            var securityManager = new ActiveMQBasicSecurityManager();
            securityManager.init(Map.of(BOOTSTRAP_USER_FILE, "jms/users.properties",
                                        BOOTSTRAP_ROLE_FILE, "jms/roles.properties"));
            EMBEDDED_ACTIVE_MQ.setSecurityManager(securityManager);
            EMBEDDED_ACTIVE_MQ.start();
        }

        void onStop(@Observes ShutdownEvent event) throws Exception {
            EMBEDDED_ACTIVE_MQ.stop();
        }
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.artemis.url", ARTEMIS_URL,
                    "quarkus.artemis.username", CONSUMER_USERNAME,
                    "quarkus.artemis.password", CONSUMER_PASSWORD
            );
        }
    }

    @Mock
    @ApplicationScoped
    static class MockPaymentService extends PaymentService {

        MockPaymentService() {
            super(null);
        }

        @Override
        public void save(Payment payment) {
            messagesLatch.countDown();
        }
    }

}