package com.sanosysalvos.kafkaadmin.config;

import com.sanosysalvos.kafkaadmin.dto.MascotaAlertaDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
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
public class KafkaConfig {

    @Bean
    public AdminClient adminClient(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
            String bootstrapServers) {
        return AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public ConsumerFactory<String, MascotaAlertaDTO> alertaConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
            String bootstrapServers) {

        JsonDeserializer<MascotaAlertaDTO> jsonDeserializer =
                new JsonDeserializer<>(MascotaAlertaDTO.class);
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.addTrustedPackages(
                "com.sanosysalvos.coincidencias.dto",
                "com.sanosysalvos.kafkaadmin.dto");

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "sanosysalvos-alertas");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MascotaAlertaDTO>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, MascotaAlertaDTO> alertaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, MascotaAlertaDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(alertaConsumerFactory);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) ->
                        org.slf4j.LoggerFactory
                                .getLogger(KafkaConfig.class)
                                .error("[KAFKA] Alerta descartada tras agotar reintentos "
                                        + "topic={} partition={} offset={} key={}",
                                        record.topic(), record.partition(),
                                        record.offset(), record.key(), exception),
                new FixedBackOff(1000L, 3L));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
