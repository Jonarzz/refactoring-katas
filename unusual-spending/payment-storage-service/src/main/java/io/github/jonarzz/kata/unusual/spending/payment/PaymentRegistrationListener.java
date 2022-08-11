package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.Session.CLIENT_ACKNOWLEDGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;
import javax.json.JsonException;
import javax.validation.constraints.NotEmpty;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
class PaymentRegistrationListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(PaymentRegistrationListener.class);

    private final ConnectionFactory connectionFactory;
    private final JmsPaymentConfig jmsConfig;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    // single executor per node - controlled on the cluster level: more replicas => more consumers
    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();
    private final AtomicInteger connectionRetryCount;

    PaymentRegistrationListener(ConnectionFactory connectionFactory,
                                JmsPaymentConfig jmsConfig,
                                ObjectMapper objectMapper,
                                PaymentService paymentService) {
        this.connectionFactory = connectionFactory;
        this.jmsConfig = jmsConfig;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;

        connectionRetryCount = new AtomicInteger(jmsConfig.connectionRetry().maxTimes());
    }

    @Override
    public void run() {
        var clientId = jmsConfig.clientId();
        var sourceDestinationName = getSourceDestinationName();
        try (var context = connectionFactory.createContext(CLIENT_ACKNOWLEDGE)) {
            context.setClientID(clientId);
            var sourceTopic = context.createTopic(sourceDestinationName);
            var targetTopic = context.createTopic(getTargetDestinationName());
            var producer = context.createProducer();
            try (var consumer = context.createDurableConsumer(sourceTopic, "consumer")) {
                LOG.debugf("Connected to destination %s, polling...", sourceDestinationName);
                connectionRetryCount.set(jmsConfig.connectionRetry().maxTimes());
                while (true) {
                    var message = consumer.receive();
                    if (message == null) {
                        LOG.infof("Consumer for destination %s is closed, returning", sourceDestinationName);
                        return;
                    }
                    var messageBody = message.getBody(String.class);
                    LOG.tracef("Polled message from destination %s: %s", sourceDestinationName, messageBody);
                    processEvent(messageBody, producer, targetTopic);
                    message.acknowledge();
                }
            }
        } catch (JMSException | JMSRuntimeException exception) {
            LOG.errorf(exception, "Consumption from %s failed", sourceDestinationName);
            waitBeforeReconnecting();
        } catch (Exception exception) {
            LOG.errorf(exception, "Handling an event polled from %s failed", sourceDestinationName);
            throw exception;
        } finally {
            resubmitListener();
        }
    }

    void onStart(@Observes StartupEvent event) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

    private void processEvent(String paymentRegisteredBody, JMSProducer producer, Topic paymentStoredTopic)
            throws JMSException {
        PaymentRegisteredEvent paymentRegistered;
        try {
            paymentRegistered = objectMapper.readValue(paymentRegisteredBody, PaymentRegisteredEvent.class);
        } catch (JsonProcessingException exception) {
            throw new JsonException(exception.getMessage());
        }
        paymentService.save(paymentRegistered);
        var paymentDetails = paymentRegistered.details();
        var paymentStored = new PaymentStoredEvent(paymentRegistered.payerUsername(),
                                                   paymentDetails.timestamp(),
                                                   paymentDetails.cost().toString());
        String paymentStoredBody;
        try {
            paymentStoredBody = objectMapper.writeValueAsString(paymentStored);
        } catch (JsonProcessingException exception) {
            throw new JsonException(exception.getMessage());
        }
        producer.send(paymentStoredTopic, paymentStoredBody);
        LOG.tracef("Sent message to destination %s: %s", paymentStoredTopic.getTopicName(), paymentStoredBody);
    }

    private void waitBeforeReconnecting() {
        if (connectionRetryCount.decrementAndGet() == 0) {
            var message = "JMS consumption failed " + jmsConfig.connectionRetry().maxTimes() + " times, aborting";
            LOG.error(message);
            throw new IllegalStateException(message);
        }
        try {
            SECONDS.sleep(jmsConfig.connectionRetry().intervalSeconds());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void resubmitListener() {
        var destinationName = getSourceDestinationName();
        if (connectionRetryCount.get() <= 0) {
            LOG.infof("Tried connecting to %s %s times, but failed - not scheduling anymore",
                      destinationName, jmsConfig.connectionRetry().maxTimes());
            return;
        }
        LOG.debugf("Scheduling polling events from %s...", destinationName);
        scheduler.submit(this);
    }

    private String getSourceDestinationName() {
        return jmsConfig.destination().register();
    }

    private String getTargetDestinationName() {
        return jmsConfig.destination().store();
    }

    @ConfigMapping(prefix = "jms.payment")
    public interface JmsPaymentConfig {

        // cannot be in a separate file with default visibility
        // BeanValidationConfigValidator throws an exception (the interface has to be public)

        @NotEmpty
        Destination destination();

        @NotEmpty
        String clientId();

        ConnectionRetry connectionRetry();

        interface Destination {

            @NotEmpty
            String register();

            @NotEmpty
            String store();
        }

        interface ConnectionRetry {

            @WithDefault("3")
            int maxTimes();

            @WithDefault("5")
            long intervalSeconds();
        }
    }
}
