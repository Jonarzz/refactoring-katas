package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
class PaymentRegistrationListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(PaymentRegistrationListener.class);

    private static final int MAX_RETRIES = 3;

    // normally would be injected - initialized inline for simplicity
    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private final ConnectionFactory connectionFactory;
    private final String queueName;
    // should be configurable - value inlined for simplicity
    private final AtomicInteger connectionRetryCount = new AtomicInteger(MAX_RETRIES);

    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;

    PaymentRegistrationListener(
            ConnectionFactory connectionFactory,
            @ConfigProperty(name = "jms.payment.queue", defaultValue = "paymentQueue") String queueName,
            ObjectMapper objectMapper,
            PaymentService paymentService) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @Override
    public void run() {
        try (var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE);
             var consumer = context.createConsumer(context.createQueue(queueName))) {
            connectionRetryCount.set(MAX_RETRIES);
            while (true) {
                LOG.debugf("Connected to queue %s, polling...", queueName);
                var message = consumer.receive();
                if (message == null) {
                    LOG.debugf("Consumer for queue %s is closed, returning", queueName);
                    return;
                }
                var messageBody = message.getBody(String.class);
                LOG.debugf("Polled message from %s queue: %s", queueName, messageBody);
                processEvent(messageBody);
            }
        } catch (JMSException | JMSRuntimeException exception) {
            LOG.errorf(exception, "Consumption from %s failed", queueName);
            retryConnection();
        } catch (Exception exception) {
            LOG.errorf(exception, "Handling an event polled from %s failed", queueName);
            throw exception;
        }
    }

    void onStart(@Observes StartupEvent event) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

    private void processEvent(String messageBody) {
        PaymentRegisteredEvent paymentEvent;
        try {
            paymentEvent = objectMapper.readValue(messageBody, PaymentRegisteredEvent.class);
        } catch (JsonProcessingException exception) {
            LOG.errorf(exception, "Failed to parse");
            throw new IllegalStateException(exception);
        }
        paymentService.save(paymentEvent);
    }

    private void retryConnection() {
        if (connectionRetryCount.decrementAndGet() == 0) {
            var message = "JMS consumption failed " + MAX_RETRIES + " times, aborting";
            LOG.error(message);
            throw new IllegalStateException(message);
        }
        try {
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LOG.debugf("Retrying connection to %s...", queueName);
        run();
    }
}
