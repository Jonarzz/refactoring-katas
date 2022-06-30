package io.github.jonarzz.kata.unusual.spending.payment;

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

@ApplicationScoped
class PaymentRegistrationListener implements Runnable {

    private static final Logger LOG = Logger.getLogger(PaymentRegistrationListener.class);

    // normally would be injected - initialized inline for simplicity
    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private final ConnectionFactory connectionFactory;
    private final String queueName;

    private final PaymentService paymentService;

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
            while (true) {
                var message = consumer.receive();
                if (message == null) {
                    return;
                }
                LOG.info(message.getBody(String.class));
                // TODO paymentService.save
            }
        } catch (Exception exception) {
            LOG.error("Consumption from " + queueName + " failed", exception);
            throw new IllegalStateException(exception);
        }
    }

    void onStart(@Observes StartupEvent event) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent event) {
        scheduler.shutdown();
    }

}
