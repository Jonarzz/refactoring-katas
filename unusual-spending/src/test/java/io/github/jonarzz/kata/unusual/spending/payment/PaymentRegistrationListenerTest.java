package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_ROLE_FILE;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_USER_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.INTEGER;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
    static Collection<PaymentRegisteredEvent> polledEvents = new ArrayList<>();

    static class MessageData {

        static final String ID = "7b8d8c9f-a8fb-486d-9c44-96008b30118e";
        static final String TIMESTAMP_STRING = "2022-07-02T12:13:49+02";
        static final OffsetDateTime TIMESTAMP = OffsetDateTime.parse(TIMESTAMP_STRING);
        static final String CATEGORY = "travel";
        static final BigDecimal AMOUNT = BigDecimal.valueOf(375.89);
        static final String CURRENCY = "USD";
    }

    @BeforeEach
    void setUp() {
        messagesLatch = new CountDownLatch(SINGLE_RUN_MESSAGE_COUNT);
        polledEvents.clear();

        try (var connectionFactory = new ActiveMQConnectionFactory(ARTEMIS_URL, PRODUCER_USERNAME, PRODUCER_PASSWORD);
             var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE)) {
            var paymentQueue = context.createQueue("paymentQueue");
            var producer = context.createProducer();
            for (int payerId = 1; payerId <= SINGLE_RUN_MESSAGE_COUNT; payerId++) {
                // TODO currency as object is not parsed properly
                producer.send(paymentQueue, """
                        {
                          "id": "%s",
                          "timestamp": "%s",
                          "payerId": %d,
                          "details": {
                            "category": "%s",
                            "cost": {
                               "amount": %s,
                               "currency": "%s"
                            }
                          }
                        }
                        """.formatted(MessageData.ID,
                                      MessageData.TIMESTAMP_STRING,
                                      payerId,
                                      MessageData.CATEGORY,
                                      MessageData.AMOUNT,
                                      MessageData.CURRENCY));
            }
        }
    }

    @RepeatedTest(5)
    void messagesAreConsumed() throws InterruptedException {
        assertThat(messagesLatch.await(1, SECONDS))
                .as("Latch counting %d messages stopped at %d",
                    SINGLE_RUN_MESSAGE_COUNT, messagesLatch.getCount())
                .isTrue();

        assertThat(polledEvents)
                .as("Polled events")
                .hasSize(SINGLE_RUN_MESSAGE_COUNT)
                .allSatisfy(paymentEvent -> assertThat(paymentEvent)
                        .returns(MessageData.ID, event -> event.id().toString())
                        .returns(MessageData.TIMESTAMP, PaymentRegisteredEvent::timestamp)
                        .satisfies(event -> assertThat(event.details())
                                .returns(MessageData.CATEGORY, details -> details.category()
                                                                                 .toString())
                                .extracting(PaymentDetails::cost)
                                .returns(MessageData.AMOUNT, Cost::amount)
                                .extracting(Cost::currency)
                                .returns(MessageData.CURRENCY, Currency::alphaCode))
                        .extracting(PaymentRegisteredEvent::payerId)
                        .extracting(BigInteger::intValue, INTEGER)
                        .isBetween(1, SINGLE_RUN_MESSAGE_COUNT));
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
        public void save(PaymentRegisteredEvent paymentEvent) {
            messagesLatch.countDown();
            polledEvents.add(paymentEvent);
        }
    }

}