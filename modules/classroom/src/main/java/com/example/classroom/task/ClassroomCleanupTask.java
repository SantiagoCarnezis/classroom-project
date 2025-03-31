package com.example.classroom.task;

import com.example.classroom.entity.Classroom;
import com.example.classroom.exception.ClassroomNotFoundException;
import com.example.classroom.kafka.CustomKafkaContainerRegistry;
import com.example.classroom.repository.ClassroomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class ClassroomCleanupTask extends TimerTask {

    private final String roomId;
    private final ClassroomRepository classroomRepository;
    private final CustomKafkaContainerRegistry customKafkaContainerRegistry;
    private final Timer timer;
    private final Logger logger = LoggerFactory.getLogger(ClassroomCleanupTask.class);

    public ClassroomCleanupTask(String roomId, ClassroomRepository classroomRepository,
                                CustomKafkaContainerRegistry customKafkaContainerRegistry, Timer timer) {

        this.roomId = roomId;
        this.classroomRepository = classroomRepository;
        this.customKafkaContainerRegistry = customKafkaContainerRegistry;
        this.timer = timer;
    }

    @Override
    public void run() {

        Classroom classroom = this.getRoom(roomId);
        logger.warn("Cleaning classroom {} after {} seconds", classroom.getId(), classroom.getDuration());
        classroom.removeAll();
        classroomRepository.save(classroom);

        if (!classroom.isPaused()) {
            customKafkaContainerRegistry.resumeListener(roomId);
        }

        timer.cancel();
    }

    private Classroom getRoom(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ClassroomNotFoundException(id));
    }
}

