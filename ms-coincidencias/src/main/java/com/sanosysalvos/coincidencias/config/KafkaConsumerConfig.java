package com.sanosysalvos.coincidencias.config;

import com.sanosysalvos.coincidencias.dto.MascotaEventoDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final String TRUSTED_PACKAGE =
            "com.sanosysalvos.mascotas.dto";

    @Bean
    public ConsumerFactory<String, MascotaEventoDTO> consumidorMascotas(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
            String bootstrapServers) {

        JsonDeserializer<MascotaEventoDTO> jsonDeserializer =
                new JsonDeserializer<>(MascotaEventoDTO.class);
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.addTrustedPackages(TRUSTED_PACKAGE);

        ErrorHandlingDeserializer<MascotaEventoDTO> valueDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,
                "sanosysalvos-coincidencias");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JsonDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MascotaEventoDTO>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, MascotaEventoDTO> consumidorMascotas) {

        ConcurrentKafkaListenerContainerFactory<String, MascotaEventoDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumidorMascotas);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (consumerRecord, exception) -> {
                    org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record =
                            (org.apache.kafka.clients.consumer.ConsumerRecord<?, ?>) consumerRecord;
                    org.slf4j.LoggerFactory
                            .getLogger(KafkaConsumerConfig.class)
                            .error("[KAFKA] Agotados los reintentos; registro descartado "
                                    + "topic={} partition={} offset={} key={}",
                                    record.topic(), record.partition(), record.offset(),
                                    record.key(), exception);
                },
                new FixedBackOff(1000L, 3L));
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
