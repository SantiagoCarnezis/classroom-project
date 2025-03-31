package com.example.queue.kafka;

import com.example.queue.dto.*;
import com.example.queue.entity.Queue;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class KafkaProducerService {

    private final KafkaTemplate<String, DtoJoinClassroom> newUserProducer;
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public void produceNewUserEvent(DtoJoinQueue dto, Queue queue) {

        DtoJoinClassroom dtoJoinClassroom = new DtoJoinClassroom();
        dtoJoinClassroom.setUserId(dto.getUserId());
        dtoJoinClassroom.setRoomId(queue.getRoomId());
        dtoJoinClassroom.setName(dto.getName());
        dtoJoinClassroom.setEmail(dto.getEmail());

        ProducerRecord<String, DtoJoinClassroom> record = new ProducerRecord<>(queue.getKafkaTopic(), dtoJoinClassroom);

        logger.info("Sending user {} for classroom {}", dto.getEmail(), queue.getRoomId());

        newUserProducer.send(record);
    }
}

