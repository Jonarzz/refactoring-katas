package io.github.jonarzz.kata.unusual.spending.payment;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.Mock;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Mock
@ApplicationScoped
class TestAggregatingPaymentService extends PaymentService {

    @ConfigProperty(name = "test.single-run-event-count", defaultValue = "10")
    int singleRunEventCount;

    private CountDownLatch processingLatch;
    private Collection<PaymentRegisteredEvent> processedEvents;

    TestAggregatingPaymentService() {
        this(null);
    }

    @Inject
    TestAggregatingPaymentService(PaymentRepository paymentRepository) {
        super(null, paymentRepository);
    }

    @Override
    public void save(PaymentRegisteredEvent paymentEvent) {
        processingLatch.countDown();
        processedEvents.add(paymentEvent);
    }

    @PostConstruct
    void init() {
        processingLatch = new CountDownLatch(singleRunEventCount);
        processedEvents = new ArrayList<>(singleRunEventCount);
    }

    void awaitForEvents(int timeout, TimeUnit units) {
        try {
            assertThat(processingLatch.await(timeout, units))
                    .as("Latch counting %d messages stopped at %d",
                        singleRunEventCount, processingLatch.getCount())
                    .isTrue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    Collection<PaymentRegisteredEvent> getProcessedEvents() {
        return processedEvents;
    }

    void reset() {
        processingLatch = new CountDownLatch(singleRunEventCount);
        processedEvents.clear();
    }
}
