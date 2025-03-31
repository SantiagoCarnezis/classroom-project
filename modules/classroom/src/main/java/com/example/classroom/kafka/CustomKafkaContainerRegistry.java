package com.example.classroom.kafka;

import com.example.classroom.dto.DtoJoinClassroom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomKafkaContainerRegistry {

    private final Map<String, ConcurrentMessageListenerContainer<String, DtoJoinClassroom>> listeners = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CustomKafkaContainerRegistry.class);

    public void registerContainer(ConcurrentMessageListenerContainer<String, DtoJoinClassroom> container, String id) {

        listeners.put(id, container);
    }

    public void resumeListener(String listenerId) {
        ConcurrentMessageListenerContainer<String, DtoJoinClassroom> container = listeners.get(listenerId);

        if (container != null) {

            if (!container.isRunning()) {
                container.start();
                logger.info("Listener {} restarted", listenerId);
            }
            else {
                logger.info("Listener {} resumed", listenerId);
                container.resume();
            }
        }
    }

    public void stopListener(String listenerId) {
        ConcurrentMessageListenerContainer<String, DtoJoinClassroom> container = listeners.get(listenerId);
        if (container != null) {
            logger.info("Listener {} stopped", listenerId);
            container.pause();
        }
    }

    public void deleteListener(String listenerId) {
        ConcurrentMessageListenerContainer<String, DtoJoinClassroom> container = listeners.remove(listenerId);
        if (container != null) {
            logger.info("Listener {} deleted", listenerId);
            container.destroy();
        }
    }
}
