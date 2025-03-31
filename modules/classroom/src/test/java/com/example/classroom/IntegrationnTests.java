package com.example.classroom;

import com.example.classroom.dto.DtoCreateClassroom;
import com.example.classroom.dto.DtoJoinClassroom;
import com.example.classroom.entity.Classroom;
import com.example.classroom.repository.ClassroomRepository;
import com.example.classroom.service.QueueService;
import com.example.classroom.service.ClassroomService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class IntegrationnTests {

    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    );
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("kafka_host", kafkaContainer::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private AdminClient adminClient;
    //private KafkaProducer<String, DtoJoinClassroom> kafkaProducer;
    @MockBean
    private KafkaTemplate<String, DtoJoinClassroom> kafkaUserJoinedRoom;
    @MockBean
    private QueueService queueService;

    @Test
    public void createRoom() throws ExecutionException, InterruptedException {

        DtoCreateClassroom dto = new DtoCreateClassroom();
        dto.setName("Room1");
        dto.setMaxCapacity(30);
        dto.setDuration(10);

        Classroom classroom1 = new Classroom();
        classroom1.setId("roomId123");
        classroom1.setName(dto.getName());
        classroom1.setMaxCapacity(dto.getMaxCapacity());
        classroom1.setDuration(dto.getDuration());
        classroom1.setPaused(false);
        classroom1.setUsers(new ArrayList<>());

        classroomService.createRoom(dto);

        Optional<Classroom> savedRoom = classroomRepository.findByName(classroom1.getName());
        assertThat(savedRoom).isPresent();
        assertThat(savedRoom.get().getName()).isEqualTo("Room1");

        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> topics = topicsResult.names().get();
        assertTrue(topics.stream().anyMatch(topic -> topic.equals(savedRoom.get().getQueueKafkaTopic())));
    }

    @Test
    public void consumeNewUserEvent() {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(JsonSerializer.TYPE_MAPPINGS, "newUser:com.example.classroom.dto.DtoJoinClassroom");

        KafkaProducer<String, DtoJoinClassroom> kafkaProducer = new KafkaProducer<>(props);

        DtoCreateClassroom dto = new DtoCreateClassroom();
        dto.setName("Room2");
        dto.setMaxCapacity(30);
        dto.setDuration(10);

        Classroom classroom1 = new Classroom();
        classroom1.setName(dto.getName());
        classroom1.setMaxCapacity(dto.getMaxCapacity());
        classroom1.setDuration(dto.getDuration());
        classroom1.setPaused(false);
        classroom1.setUsers(new ArrayList<>());

        classroomService.createRoom(dto);

        Optional<Classroom> savedRoom = classroomRepository.findByName(classroom1.getName());
        assertThat(savedRoom).isPresent();
        Classroom classroom2 = savedRoom.get();

        String userEmail = "example@email.com";
        DtoJoinClassroom dtoJoinClassroom = new DtoJoinClassroom();
        dtoJoinClassroom.setRoomId(classroom2.getId());
        dtoJoinClassroom.setName(dto.getName());
        dtoJoinClassroom.setEmail(userEmail);

        ProducerRecord<String, DtoJoinClassroom> record = new ProducerRecord<>(classroom2.getQueueKafkaTopic(), dtoJoinClassroom);

        kafkaProducer.send(record);

        await()
        .pollInterval(Duration.ofSeconds(5))
        .untilAsserted(() -> {

            Classroom classroom3 = classroomRepository.findByName(classroom1.getName()).get();
            assertTrue(classroom3.getUsers().stream().anyMatch(user -> user.getEmail().equals(userEmail)));

        });

    }
}

