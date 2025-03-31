package com.example.queue.service;

import com.example.queue.dto.*;
import com.example.queue.entity.Queue;
import com.example.queue.entity.UserInQueue;
import com.example.queue.kafka.KafkaProducerService;
import com.example.queue.repository.QueueRepository;
import com.example.queue.repository.UserInQueueRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class QueueService {

    private final QueueRepository queueRepository;
    private final UserInQueueRepository userInQueueRepository;
    private final KafkaProducerService kafkaProducerService;
    private final Logger logger = LoggerFactory.getLogger(QueueService.class);

    public List<Queue> getAll() {
        return queueRepository.findAll();
    }

    public List<UserInQueue> getUserInQueue(String queueId) {
        return userInQueueRepository.findAllByQueueId(queueId);
    }

    public Queue createQueue(DtoCreateQueue dto) {

        Queue queue = new Queue();
        queue.setRoomId(dto.getRoomId());
        queue.setKafkaTopic(dto.getKafkaTopic());
        queue.setPaused(false);
        queue = queueRepository.save(queue);

        logger.info("Queue {} created for classroom {}", queue.getId(), queue.getRoomId());

        return queue;
    }

    public void deleteQueue(String id) {

        queueRepository.deleteByRoomId(id);
        logger.info("Queue {} deleted", id);
    }

    public long joinQueue(DtoJoinQueue dto)
    {

        Queue queue = getQueue(dto.getQueueId());

        UserInQueue userInQueue = new UserInQueue();
        userInQueue.setEmail(dto.getEmail());
        userInQueue.setName(dto.getName());
        userInQueue.setQueueId(queue.getId());
        userInQueue.setJoiningDate(LocalDateTime.now());

        userInQueueRepository.save(userInQueue);

        logger.info("{} has joined queue {}", dto.getEmail(), dto.getQueueId());

        long position = queueRepository.count();

        kafkaProducerService.produceNewUserEvent(dto, queue);

        return position;
    }

    public void joinQueueMultipleUsers(DtoJoinQueueAll dtoJoinQueueAll)
    {
        Queue queue = getQueue(dtoJoinQueueAll.getQueueId());

        String email;
        for (int i = 1; i <= dtoJoinQueueAll.getAmount(); i++) {

            email = "user" + i + "@example." + dtoJoinQueueAll.getQueueId();

            UserInQueue userInQueue = new UserInQueue();
            userInQueue.setEmail(email);
            userInQueue.setName(email);
            userInQueue.setQueueId(dtoJoinQueueAll.getQueueId());
            userInQueue.setJoiningDate(LocalDateTime.now());

            userInQueue = userInQueueRepository.save(userInQueue);

            DtoJoinQueue dto = new DtoJoinQueue();
            dto.setUserId(userInQueue.getId());
            dto.setEmail(email);
            dto.setName(email);
            dto.setQueueId(dto.getQueueId());

            kafkaProducerService.produceNewUserEvent(dto, queue);
        }

        logger.info("Multiple users have joined queue {}", dtoJoinQueueAll.getQueueId());
    }

    private Queue getQueue(String id) {
        return queueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Queue not found for id: " + id));
    }

}
