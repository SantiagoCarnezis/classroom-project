package com.example.classroom.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomRebalanceListener implements ConsumerRebalanceListener {

    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(CustomRebalanceListener.class);

    public CustomRebalanceListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        partitions.forEach(partition -> {

            logger.info("Partition {} for topic {} revoked", partition, partition.topic());

            meterRegistry.counter(
                    "kafka.partition.revoked.count",
                    "topic", partition.topic(),
                    "partition", String.valueOf(partition.partition())
            ).increment();
        });
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        partitions.forEach(partition -> {

            logger.info("Partition {} for topic {} assigned", partition, partition.topic());

            meterRegistry.counter(
                    "kafka.partition.assigned.count",
                    "topic", partition.topic(),
                    "partition", String.valueOf(partition.partition())
            ).increment();
        });
    }
}

