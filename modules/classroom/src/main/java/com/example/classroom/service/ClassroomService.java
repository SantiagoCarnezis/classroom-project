package com.example.classroom.service;

import com.example.classroom.config.CustomKafkaListenerFactory;
import com.example.classroom.dto.DtoCreateClassroom;
import com.example.classroom.entity.Classroom;
import com.example.classroom.exception.ClassroomNotFoundException;
import com.example.classroom.kafka.CustomKafkaContainerRegistry;
import com.example.classroom.kafka.KafkaTopicService;
import com.example.classroom.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClassroomService {

    private final KafkaTopicService kafkaTopicService;
    private final CustomKafkaListenerFactory customKafkaListenerFactory;
    private final CustomKafkaContainerRegistry customKafkaContainerRegistry;
    private final ClassroomRepository classroomRepository;
    private final QueueService queueService;
    private final Logger logger = LoggerFactory.getLogger(ClassroomService.class);

    public Classroom createRoom(DtoCreateClassroom dto) {

        Classroom classroom = new Classroom();
        classroom.setUsers(new ArrayList<>());
        classroom.setName(dto.getName());
        classroom.setMaxCapacity(dto.getMaxCapacity());
        classroom.setDuration(dto.getDuration());
        classroom = classroomRepository.save(classroom);

        String queueKafkaTopic = kafkaTopicService.createTopic(classroom.getId());
        customKafkaListenerFactory.createListener(queueKafkaTopic, classroom.getId());

        classroom.setQueueKafkaTopic(queueKafkaTopic);
        classroom = classroomRepository.save(classroom);

        logger.info("classroom created {}", classroom.getId());

        queueService.createQueue(classroom);

        return classroom;
    }

    public void deleteRoom(String roomId) {

        classroomRepository.deleteById(roomId);
        logger.info("classroom deleted {}", roomId);

        kafkaTopicService.deleteTopic(roomId);
        customKafkaContainerRegistry.deleteListener(roomId);
        queueService.deleteQueue(roomId);
    }

    public void pause(String roomId)
    {
        Classroom classroom = getRoom(roomId);
        logger.info("classroom stopped {}", roomId);
        classroom.setPaused(true);
        classroomRepository.save(classroom);

        customKafkaContainerRegistry.stopListener(roomId);
    }

    public void resume(String roomId)
    {
        Classroom classroom = getRoom(roomId);
        classroom.setPaused(false);
        classroomRepository.save(classroom);

        customKafkaContainerRegistry.resumeListener(roomId);
    }

    public void resumeAllRooms()
    {
        List<Classroom> classrooms = classroomRepository.findAll();

        for (Classroom classroom : classrooms) {

            classroom.setPaused(false);
            classroomRepository.save(classroom);
            customKafkaContainerRegistry.resumeListener(classroom.getId());
        }
    }

    public List<Classroom> getAllRooms() {
        return classroomRepository.findAll();
    }

    private Classroom getRoom(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ClassroomNotFoundException(id));
    }
}

