package io.github.jonarzz.kata.unusual.spending.payment;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static io.github.jonarzz.kata.unusual.spending.payment.PaymentRegistrationListener.JmsPaymentConfig;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_ROLE_FILE;
import static org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager.BOOTSTRAP_USER_FILE;

import io.quarkus.runtime.StartupEvent;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.spi.core.security.ActiveMQBasicSecurityManager;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;

@Singleton
class TestJmsServer {

    private static final EmbeddedActiveMQ EMBEDDED_ACTIVE_MQ = new EmbeddedActiveMQ();

    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    // see: broker.xml resource and properties files in the jms/ subdirectory
    static class Config {

        static final String URL = "vm://localhost:61616";

        static class Producer {

            static final String USERNAME = "producer";
            static final String PASSWORD = "producer123";
        }

        static class Consumer {

            static final String USERNAME = "consumer";
            static final String PASSWORD = "consumer123";
        }

        static class Listener {

            static final String USERNAME = "listener";
            static final String PASSWORD = "listener321";
        }
    }

    private final long connectionTimeoutSeconds;

    private final CountDownLatch singularLatch = new CountDownLatch(1);

    @SuppressWarnings("unused") // enforce failure on first connection to JMS by injecting the listener here
    TestJmsServer(PaymentRegistrationListener listener, JmsPaymentConfig config) {
        var retryConfig = config.connectionRetry();
        connectionTimeoutSeconds = 1 + retryConfig.maxTimes() * retryConfig.intervalSeconds();
    }

    static void stop() throws Exception {
        EMBEDDED_ACTIVE_MQ.getActiveMQServer()
                          .stop(true);
        EMBEDDED_ACTIVE_MQ.stop();
    }

    void awaitForConnection() {
        try {
            if (!singularLatch.await(connectionTimeoutSeconds, SECONDS)) {
                throw new IllegalStateException("No consumer connected");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    void onStart(@Observes StartupEvent event) throws Exception {
        runAsync(() -> {
            // allow listener to fail on first connection attempt
            sleepUninterruptibly(2500, MILLISECONDS);
            var securityManager = new ActiveMQBasicSecurityManager();
            securityManager.init(Map.of(BOOTSTRAP_USER_FILE, "jms/users.properties",
                                        BOOTSTRAP_ROLE_FILE, "jms/roles.properties"));
            EMBEDDED_ACTIVE_MQ.setSecurityManager(securityManager);
            try {
                EMBEDDED_ACTIVE_MQ.start();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            cleanUpPersistentQueues();
            waitForAtLeastOneConsumer();
        }).get(DEFAULT_TIMEOUT_SECONDS, SECONDS);
    }

    void cleanUpPersistentQueues() {
        var activeMqServer = EMBEDDED_ACTIVE_MQ.getActiveMQServer();
        var queues = Arrays.stream(activeMqServer.getActiveMQServerControl()
                                                 .getQueueNames())
                           .map(activeMqServer::locateQueue)
                           .filter(Objects::nonNull)
                           .toList();
        for (var queue : queues) {
            try {
                queue.deleteAllReferences();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    void blockUntilMessageCountReached(String queueName, int expectedCount) {
        var activeMqServer = EMBEDDED_ACTIVE_MQ.getActiveMQServer();
        var queue = Optional.ofNullable(activeMqServer.locateQueue(queueName))
                            .orElseThrow(() -> new IllegalStateException("Queue with name "
                                                                         + queueName + " not found"));
        blockUntil(() -> queue.getMessageCount() == expectedCount,
                   DEFAULT_TIMEOUT_SECONDS,
                   "Expected messages count %s in queue %s was not reached within %ds"
                           .formatted(expectedCount, queueName, DEFAULT_TIMEOUT_SECONDS));
    }

    private void waitForAtLeastOneConsumer() {
        new Thread(() -> blockUntil(
                () -> {
                    long consumerCount = EMBEDDED_ACTIVE_MQ.getActiveMQServer()
                                                           .getTotalConsumerCount();
                    var consumerPresent = consumerCount > 0;
                    if (consumerPresent) {
                        singularLatch.countDown();
                    }
                    return consumerPresent;
                },
                connectionTimeoutSeconds,
                "No consumer connected to the embedded Active MQ in " + connectionTimeoutSeconds + "s"
        )).start();
    }

    private static void blockUntil(BooleanSupplier action, long timeoutSeconds, String timeoutExceptionMessage) {
        var sleepTimeMs = 100;
        var timeout = timeoutSeconds * (1000 / sleepTimeMs);
        var counter = 0;
        do {
            if (action.getAsBoolean()) {
                return;
            }
            sleepUninterruptibly(sleepTimeMs, MILLISECONDS);
        } while (++counter < timeout);
        throw new IllegalStateException(timeoutExceptionMessage);
    }
}