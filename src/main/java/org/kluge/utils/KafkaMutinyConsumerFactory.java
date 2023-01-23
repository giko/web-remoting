package org.kluge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaMutinyConsumerFactory {
    public final static String DEFAULT_GROUP_ID = "remoting-default-group";
    protected Vertx vertx;
    protected ObjectMapper objectMapper;
    protected String bootstrapServers;

    public KafkaMutinyConsumerFactory(
            Vertx vertx,
            ObjectMapper objectMapper,
            @ConfigProperty(name = "kafka.bootstrap.servers") String bootstrapServers) {
        this.vertx = vertx;
        this.objectMapper = objectMapper;
        this.bootstrapServers = bootstrapServers;
    }

    public <T> Multi<T> getMultiConsumer(String topic, Class<T> tClass, String group, OffsetResetStrategy resetStrategy) {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServers);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", group);
        config.put("auto.offset.reset", resetStrategy.getValue());
        config.put("enable.auto.commit", "false");
        var consumer = KafkaConsumer.create(vertx, config);
        consumer.subscribe(topic);

        Multi<T> multi = Multi.createFrom().emitter(emitter -> consumer.handler(record -> {
            var value = (String) record.value();
            try {
                emitter.emit(objectMapper.readValue(value, tClass));
            } catch (JsonProcessingException e) {
                emitter.fail(e);
            }
        }));
        return multi
                .onCompletion().invoke(consumer::close)
                .onTermination().invoke(consumer::close);
    }

    public <T> Multi<T> getMultiConsumer(String topic, Class<T> tClass) {
        return getMultiConsumer(topic, tClass, DEFAULT_GROUP_ID, OffsetResetStrategy.NONE);
    }

    public enum OffsetResetStrategy {
        EARLIEST("earliest"),
        LATEST("latest"),
        NONE("none");

        OffsetResetStrategy(String value) {
            this.value = value;
        }

        @Getter
        private final String value;
    }

}
