package com.example.queue.kafka;

import com.example.queue.dto.DtoLeftQueue;
import com.example.queue.repository.UserInQueueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class JoinedClassroomKafkaListener {

    private final UserInQueueRepository userInQueueRepository;
    private final String joinedRoomTopic = "joined-classroom-topic";
    private final Logger logger = LoggerFactory.getLogger(JoinedClassroomKafkaListener.class);

    @KafkaListener(topics = joinedRoomTopic, groupId = "joined-classroom-group")
    public void leftQueue(DtoLeftQueue dto)
    {
        userInQueueRepository.deleteById(dto.getUserId());

        logger.info("{} is leaving the queue from classroom {}", dto.getEmail(), dto.getRoomId());
    }
}
