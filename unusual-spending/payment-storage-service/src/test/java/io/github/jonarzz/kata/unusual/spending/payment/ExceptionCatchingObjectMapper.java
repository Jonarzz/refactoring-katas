package io.github.jonarzz.kata.unusual.spending.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.All;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.quarkus.jackson.runtime.JacksonBuildTimeConfig;
import io.quarkus.jackson.runtime.ObjectMapperProducer;
import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ExceptionCatchingObjectMapper extends ObjectMapper {

    private final ObjectMapper objectMapper;

    private BlockingExceptionCaptor exceptionCaptor;

    private ExceptionCatchingObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T readValue(String src, Class<T> valueType) throws JsonProcessingException {
        try {
            return objectMapper.readValue(src, valueType);
        } catch (JsonProcessingException exception) {
            if (exceptionCaptor != null) {
                exceptionCaptor.set(exception);
            }
            throw exception;
        }
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            if (exceptionCaptor != null) {
                exceptionCaptor.set(exception);
            }
            throw exception;
        }
    }

    BlockingExceptionCaptor blockingExceptionCaptor() {
        return exceptionCaptor = new BlockingExceptionCaptor();
    }

    @Mock
    @ApplicationScoped
    public static class ObjectMapperProducerMock extends ObjectMapperProducer {

        @Mock
        @Singleton
        @Produces
        @Override
        public ObjectMapper objectMapper(@All List<ObjectMapperCustomizer> customizers,
                                         JacksonBuildTimeConfig jacksonBuildTimeConfig) {
            var objectMapper = super.objectMapper(customizers, jacksonBuildTimeConfig);
            return new ExceptionCatchingObjectMapper(objectMapper);
        }
    }

    static class BlockingExceptionCaptor {

        private Exception exception;

        synchronized Exception getWithin(int value, TimeUnit unit) throws InterruptedException {
            wait(unit.toMillis(value));
            return exception;
        }

        private synchronized void set(Exception exception) {
            this.exception = exception;
            notify();
        }

    }
}
