package com.example.classroom.config;

import com.example.classroom.dto.DtoJoinClassroom;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka_host}")
    private String KAFKA_HOST;

    @Bean
    public ConsumerFactory<String, DtoJoinClassroom> newUserConsumerFactory() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "600000");

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.classroom.dto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.classroom.dto.DtoJoinClassroom");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "newUser:com.example.classroom.dto.DtoJoinClassroom");


        //return new DefaultKafkaConsumerFactory<>(props);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(DtoJoinClassroom.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DtoJoinClassroom> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, DtoJoinClassroom> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(newUserConsumerFactory());
        factory.setConcurrency(10);

        return factory;
    }

    @Bean
    public AdminClient adminClient() {

        return AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST
        ));
    }
}