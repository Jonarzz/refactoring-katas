package io.github.jonarzz.kata.unusual.spending.payment;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jms.ConnectionFactory;
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

    private final PaymentService paymentService;

    // should be configurable - value inlined for simplicity
    private final AtomicInteger connectionRetryCount = new AtomicInteger(MAX_RETRIES);

    PaymentRegistrationListener(
            ConnectionFactory connectionFactory,
            @ConfigProperty(name = "jms.payment.queue", defaultValue = "paymentQueue") String queueName,
            PaymentService paymentService) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.paymentService = paymentService;
    }

    @Override
    public void run() {
        try (var context = connectionFactory.createContext(AUTO_ACKNOWLEDGE)) {
            var consumer = context.createConsumer(context.createQueue(queueName));
            connectionRetryCount.set(MAX_RETRIES);
            while (true) {
                var message = consumer.receive();
                if (message == null) {
                    LOG.debugf("Consumer for queue %s is closed, returning", queueName);
                    return;
                }
                LOG.info(message.getBody(String.class));
                paymentService.save(null); // TODO unmarshall the body and pass here
            }
        } catch (Exception exception) {
            LOG.errorf("Consumption from %s failed, reason: %s", queueName, exception);
            if (connectionRetryCount.decrementAndGet() == 0) {
                throw new IllegalStateException("JMS consumption failed " + MAX_RETRIES + " times, aborting");
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

    void onStart(@Observes StartupEvent event) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

}
