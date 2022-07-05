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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

@QuarkusTest
@TestProfile(PaymentRegistrationListenerTest.ConfigProfile.class)
class PaymentRegistrationListenerTest {

    static final String ARTEMIS_URL = "vm://localhost:61616";
    // see: broker.xml resource and properties files in the jms/ subdirectory
    static final String PRODUCER_USERNAME = "producer";
    static final String PRODUCER_PASSWORD = "producer123";
    static final String CONSUMER_USERNAME = "consumer";
    static final String CONSUMER_PASSWORD = "consumer321";

    static final EmbeddedActiveMQ EMBEDDED_ACTIVE_MQ = new EmbeddedActiveMQ();

    static final int SINGLE_RUN_EVENT_COUNT = 10;

    static CountDownLatch messagesLatch = new CountDownLatch(SINGLE_RUN_EVENT_COUNT);
    static Collection<PaymentRegisteredEvent> polledEvents = new ArrayList<>();

    static class MessageData {

        static final String ID = "7b8d8c9f-a8fb-486d-9c44-96008b30118e";
        static final String TIMESTAMP_STRING = "2022-07-02T12:13:49+02";
        static final OffsetDateTime TIMESTAMP = OffsetDateTime.parse(TIMESTAMP_STRING);
        static final String CATEGORY = "travel";
        static final BigDecimal AMOUNT = BigDecimal.valueOf(375.89);
        static final String CURRENCY = "USD";
        static final String LANGUAGE_TAG = "en-US";
        static final Locale LOCALE = Locale.US;
    }

    @BeforeEach
    void setUp() {
        messagesLatch = new CountDownLatch(SINGLE_RUN_EVENT_COUNT);
        polledEvents.clear();
    }

    @Nested
    class EventsAreConsumed {

        @RepeatedTest(5)
        void currencyAsStringValue() throws InterruptedException {
            var currencyJsonValue = "\"" + MessageData.CURRENCY + "\"";
            sendEvents(payerId -> createMessageWithExplicitCurrencyJsonPart(payerId, currencyJsonValue));

            awaitForEventsToBePolled();

            assertPolledEvents();
        }

        @RepeatedTest(5)
        void currencyAsJsonObject() throws InterruptedException {
            var currencyJson = """
                      {
                      "alphaCode": "%s",
                      "languageTag": "%s"
                    }""".formatted(MessageData.CURRENCY,
                                   MessageData.LANGUAGE_TAG);
            sendEvents(payerId -> createMessageWithExplicitCurrencyJsonPart(payerId, currencyJson));

            awaitForEventsToBePolled();

            assertPolledEvents();
        }

    }

    private static void sendEvents(Function<Integer, String> messageForIterationCreator) {
        try (var connectionFactory = new ActiveMQConnectionFactory(ARTEMIS_URL, PRODUCER_USERNAME, PRODUCER_PASSWORD);
             var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE)) {
            var paymentQueue = context.createQueue("paymentQueue");
            var producer = context.createProducer();
            for (int payerId = 1; payerId <= SINGLE_RUN_EVENT_COUNT; payerId++) {
                producer.send(paymentQueue, messageForIterationCreator.apply(payerId));
            }
        }
    }

    private static String createMessageWithExplicitCurrencyJsonPart(int payerId, String currencyJsonPart) {
        return """
                {
                  "id": "%s",
                  "payerId": "%s",
                  "timestamp": "%s",
                  "details": {
                    "category": "%s",
                    "cost": {
                       "amount": %s,
                       "currency": %s
                    }
                  }
                }
                """.formatted(MessageData.ID,
                              payerId,
                              MessageData.TIMESTAMP_STRING,
                              MessageData.CATEGORY,
                              MessageData.AMOUNT,
                              currencyJsonPart);
    }

    private void awaitForEventsToBePolled() throws InterruptedException {
        assertThat(messagesLatch.await(1, SECONDS))
                .as("Latch counting %d messages stopped at %d",
                    SINGLE_RUN_EVENT_COUNT, messagesLatch.getCount())
                .isTrue();
    }

    private void assertPolledEvents() {
        assertThat(polledEvents)
                .as("Polled events")
                .hasSize(SINGLE_RUN_EVENT_COUNT)
                .allSatisfy(paymentEvent -> assertThat(paymentEvent)
                        .returns(MessageData.ID, event -> event.id().toString())
                        .returns(MessageData.TIMESTAMP, PaymentRegisteredEvent::timestamp)
                        .satisfies(event -> assertThat(event.details())
                                .returns(MessageData.CATEGORY, details -> details.category()
                                                                                 .toString())
                                .extracting(PaymentDetails::cost)
                                .returns(MessageData.AMOUNT, Cost::amount)
                                .extracting(Cost::currency)
                                .returns(MessageData.CURRENCY, Currency::alphaCode)
                                .returns(MessageData.LOCALE, Currency::locale))
                        .extracting(PaymentRegisteredEvent::payerId)
                        .extracting(BigInteger::intValue, INTEGER)
                        .isBetween(1, SINGLE_RUN_EVENT_COUNT));
    }

    public static class ConfigProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.artemis.url", ARTEMIS_URL,
                    "quarkus.artemis.username", CONSUMER_USERNAME,
                    "quarkus.artemis.password", CONSUMER_PASSWORD
            );
        }
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

    @Mock
    @ApplicationScoped
    static class FakePaymentService extends PaymentService {

        FakePaymentService() {
            super(null, null);
        }

        @Override
        @ActivateRequestContext
        public void save(PaymentRegisteredEvent paymentEvent) {
            messagesLatch.countDown();
            polledEvents.add(paymentEvent);
        }
    }

}