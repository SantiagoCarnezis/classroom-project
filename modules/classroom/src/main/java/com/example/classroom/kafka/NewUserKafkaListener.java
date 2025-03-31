package com.example.classroom.kafka;

import com.example.classroom.dto.DtoJoinClassroom;
import com.example.classroom.entity.Classroom;
import com.example.classroom.entity.User;
import com.example.classroom.exception.ClassroomNotFoundException;
import com.example.classroom.repository.ClassroomRepository;
import com.example.classroom.task.ClassroomCleanupTask;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;


@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class NewUserKafkaListener implements AcknowledgingConsumerAwareMessageListener<String, DtoJoinClassroom> {

    private final ClassroomRepository classroomRepository;
    private final KafkaTopicService kafkaTopicService;
    private final CustomKafkaContainerRegistry customKafkaContainerRegistry;
    private final ReentrantLock lock = new ReentrantLock();
    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(NewUserKafkaListener.class);

    @Override
    public void onMessage(ConsumerRecord<String, DtoJoinClassroom> data, Acknowledgment ack, Consumer<?, ?> consumer) {


        DtoJoinClassroom dto = data.value();

        logger.info("Starting Thread: {} | Partition: {} | User: {}",
                Thread.currentThread().getName(), data.partition(), dto.getEmail());

        lock.lock();
        Classroom classroom = getRoom(dto.getRoomId());

        logger.info("Processing user {}", dto.getEmail());

        if (classroom.isFull()) {

            logger.warn("user {} could not join classroom {} because it is full", dto.getEmail(), dto.getRoomId());
            customKafkaContainerRegistry.stopListener(classroom.getId());
            this.revertOffset(consumer, data.topic(), data.partition());
        }
        else {

            this.joinRoom(classroom, dto);

            ack.acknowledge();

            if(classroom.isFull()) {

                logger.warn("classroom {} is full", classroom.getId());
                customKafkaContainerRegistry.stopListener(classroom.getId());

                Timer timer = new Timer();

                TimerTask task = new ClassroomCleanupTask(classroom.getId(), classroomRepository, customKafkaContainerRegistry, timer);

                timer.schedule(task, classroom.getDuration() * 1000L);

            }

            kafkaTopicService.produceUserJoinedRoom(dto);
        }

        recordConsumptionLatency(data);

        lock.unlock();
    }

    private void revertOffset(Consumer<?, ?> consumer, String topic, int partition) {

        TopicPartition topicPartition = new TopicPartition(topic, partition);

        consumer.seek(topicPartition, consumer.position(topicPartition) - 1);
    }


    private void joinRoom(Classroom classroom, DtoJoinClassroom dto) {

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());

        classroom.addUser(user);

        logger.info("adding user {} to classroom {}. Current capacity: {}/{}",
                user.getEmail(), classroom.getId(), classroom.getUsers().size(), classroom.getMaxCapacity());

        classroomRepository.save(classroom);
    }

    private Classroom getRoom(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ClassroomNotFoundException(id));
    }

    private void recordConsumptionLatency(ConsumerRecord<String, DtoJoinClassroom> data) {

        long latency = System.currentTimeMillis() - data.timestamp();

        meterRegistry.timer("kafka.message.latency",
                        "topic", data.topic(),
                        "partition", String.valueOf(data.partition()))
                .record(Duration.ofMillis(latency));
    }
}
