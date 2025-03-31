package com.example.classroom.kafka;

import com.example.classroom.config.CustomKafkaListenerFactory;
import com.example.classroom.entity.Classroom;
import com.example.classroom.repository.ClassroomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class StartUpListenersService {

    private final ClassroomRepository classroomRepository;
    private final CustomKafkaListenerFactory customKafkaListenerFactory;
    private final Logger logger = LoggerFactory.getLogger(StartUpListenersService.class);

    @PostConstruct
    public void recreateListeners() {

        logger.info("Recreating rooms...");

        List<Classroom> classrooms = classroomRepository.findAll();

        classrooms.forEach(classroom ->
                customKafkaListenerFactory.createListener(classroom.getQueueKafkaTopic(), classroom.getId(), !classroom.isPaused()));
    }
}
