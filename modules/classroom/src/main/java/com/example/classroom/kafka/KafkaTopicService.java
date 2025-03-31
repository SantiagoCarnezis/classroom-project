package com.example.classroom.kafka;

import com.example.classroom.dto.DtoJoinClassroom;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class KafkaTopicService {

    private final String queueTopicPrefix = "queue-topic-";
    private final String joinedRoomTopic = "joined-classroom-topic";
    private final AdminClient adminClient;
    private final KafkaTemplate<String, DtoJoinClassroom> kafkaUserJoinedRoom;
    private final Logger logger = LoggerFactory.getLogger(KafkaTopicService.class);

    public void produceUserJoinedRoom(DtoJoinClassroom dto) {

        kafkaUserJoinedRoom.send(joinedRoomTopic, dto);
    }


    public String createTopic(String roomId) {

        String topicName = queueTopicPrefix + roomId;
        NewTopic newTopic = new NewTopic(topicName, 4, (short) 1);
        adminClient.createTopics(Collections.singleton(newTopic));
        logger.info("Topic created {}", topicName);

        return topicName;
    }

    public void deleteTopic(String roomId) {

        String topicName = queueTopicPrefix + roomId;
        adminClient.deleteTopics(Collections.singleton(topicName));
        logger.info("Topic deleted {}", topicName);
    }

}
