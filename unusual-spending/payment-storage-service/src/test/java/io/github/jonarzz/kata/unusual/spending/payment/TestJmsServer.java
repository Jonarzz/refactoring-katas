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
import java.util.concurrent.CountDownLatch;

@Singleton
class TestJmsServer {

    private static final EmbeddedActiveMQ EMBEDDED_ACTIVE_MQ = new EmbeddedActiveMQ();

    // see: broker.xml resource and properties files in the jms/ subdirectory
    static class Config {

        static final String URL = "vm://localhost:61616";

        static class Producer {

            static final String USERNAME = "producer";
            static final String PASSWORD = "producer123";
        }

        static class Consumer {

            static final String USERNAME = "consumer";
            static final String PASSWORD = "consumer321";
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
        }).get(5, SECONDS);
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

    private void waitForAtLeastOneConsumer() {
        new Thread(() -> {
            var counter = 0;
            long consumerCount;
            do {
                consumerCount = EMBEDDED_ACTIVE_MQ.getActiveMQServer()
                                                  .getTotalConsumerCount();
                if (consumerCount > 0) {
                    singularLatch.countDown();
                    return;
                }
                sleepUninterruptibly(1, SECONDS);
            } while (++counter < connectionTimeoutSeconds);
            throw new IllegalStateException("No consumer connected to the embedded Active MQ within "
                                            + connectionTimeoutSeconds + " seconds");
        }).start();
    }
}