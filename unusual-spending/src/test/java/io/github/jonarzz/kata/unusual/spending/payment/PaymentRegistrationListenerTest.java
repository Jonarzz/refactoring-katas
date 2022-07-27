package io.github.jonarzz.kata.unusual.spending.payment;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

import io.github.jonarzz.kata.unusual.spending.money.Cost;
import io.github.jonarzz.kata.unusual.spending.money.Currency;
import io.github.jonarzz.kata.unusual.spending.payment.TestJmsServer.Config;
import io.github.jonarzz.kata.unusual.spending.payment.TestJmsServer.Config.Consumer;
import io.github.jonarzz.kata.unusual.spending.payment.TestJmsServer.Config.Producer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@QuarkusTest
@TestProfile(PaymentRegistrationListenerTest.ConfigProfile.class)
class PaymentRegistrationListenerTest {

    private static final String REGISTER_PAYMENT_TOPIC_NAME = "payment/register/v1";

    private static final int SINGLE_RUN_EVENT_COUNT = 10;

    @Inject
    TestJmsServer testJmsServer;
    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    TestAggregatingPaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService.reset();
        testJmsServer.awaitForConnection();
    }

    @AfterEach
    void cleanUp() {
        testJmsServer.cleanUpPersistentQueues();
    }

    @RepeatedTest(5)
    void currencyAsStringValue() {
        var currencyJsonValue = "\"" + MessageData.CURRENCY + "\"";
        sendEvents(payerId -> createMessageWithExplicitCurrencyJsonPart(payerId, currencyJsonValue));

        paymentService.awaitForEvents(1, SECONDS);

        assertProcessedEvents();
    }

    @RepeatedTest(5)
    void currencyAsJsonObject() {
        var currencyJson = """
                  {
                  "alphaCode": "%s",
                  "languageTag": "%s"
                }""".formatted(MessageData.CURRENCY,
                               MessageData.LANGUAGE_TAG);
        sendEvents(payerId -> createMessageWithExplicitCurrencyJsonPart(payerId, currencyJson));

        paymentService.awaitForEvents(1, SECONDS);

        assertProcessedEvents();
    }

    @RepeatedTest(5)
    void invalidMessage() {
        var message = "{{test}";
        sendEvents(payerId -> message);

        // TODO find a way to spy on ObjectMapper in Quarkus tests to handle this with a latch
        //      don't want to waste any more time on it now
        sleepUninterruptibly(500, MILLISECONDS);

        assertThat(paymentService.getProcessedEvents())
                .isEmpty();
    }

    private static void sendEvents(Function<Integer, String> messageForIterationCreator) {
        try (var connectionFactory = new ActiveMQConnectionFactory(Config.URL, Producer.USERNAME, Producer.PASSWORD);
             var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE)) {
            var paymentDestination = context.createTopic(REGISTER_PAYMENT_TOPIC_NAME);
            var producer = context.createProducer();
            for (int payerId = 1; payerId <= SINGLE_RUN_EVENT_COUNT; payerId++) {
                producer.send(paymentDestination, messageForIterationCreator.apply(payerId));
            }
        }
    }

    private static String createMessageWithExplicitCurrencyJsonPart(int payerId, String currencyJsonPart) {
        return """
                {
                  "id": "%s",
                  "payerId": "%s",
                  "details": {
                    "timestamp": "%s",
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

    private void assertProcessedEvents() {
        assertThat(paymentService.getProcessedEvents())
                .as("Processed events")
                .hasSize(SINGLE_RUN_EVENT_COUNT)
                .allSatisfy(paymentEvent -> assertThat(paymentEvent)
                        .returns(MessageData.ID, event -> event.id().toString())
                        .satisfies(event -> assertThat(event.details())
                                .returns(MessageData.CATEGORY, details -> details.category()
                                                                                 .toString())
                                .returns(MessageData.TIMESTAMP, PaymentDetails::timestamp)
                                .extracting(PaymentDetails::cost)
                                .returns(MessageData.AMOUNT, Cost::getAmount)
                                .extracting(Cost::getCurrency)
                                .returns(MessageData.CURRENCY, Currency::alphaCode)
                                .returns(MessageData.LOCALE, Currency::locale))
                        .extracting(PaymentRegisteredEvent::payerId)
                        .asInstanceOf(LONG)
                        .isBetween(1L, (long) SINGLE_RUN_EVENT_COUNT));
    }

    public static class ConfigProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "test.single-run-event-count", String.valueOf(SINGLE_RUN_EVENT_COUNT),
                    "quarkus.artemis.url", Config.URL,
                    "quarkus.artemis.username", Consumer.USERNAME,
                    "quarkus.artemis.password", Consumer.PASSWORD,
                    "jms.payment.destination", REGISTER_PAYMENT_TOPIC_NAME,
                    "jms.payment.client-id", "test-client",
                    "jms.payment.connection-retry.interval-seconds", "3",
                    "quarkus.log.category.\"org.apache.activemq\".level", "ERROR",
                    "quarkus.liquibase.migrate-at-start", "false"
            );
        }
    }

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

}