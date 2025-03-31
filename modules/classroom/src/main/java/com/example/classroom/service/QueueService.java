package com.example.classroom.service;

import com.example.classroom.dto.DtoCreateQueue;
import com.example.classroom.dto.DtoQueue;
import com.example.classroom.entity.Classroom;
import com.example.classroom.exception.QueueCreationException;
import com.example.classroom.exception.QueueDeletionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class QueueService {

    @Value("${service.queue.host}")
    private String QUEUE_SERVICE_URL;
    private final Logger logger = LoggerFactory.getLogger(QueueService.class);
    private final WebClient webClient;

    public Disposable createQueue(Classroom classroom) {

        DtoCreateQueue dtoCreateQueue = new DtoCreateQueue();
        dtoCreateQueue.setRoomId(classroom.getId());
        dtoCreateQueue.setKafkaTopic(classroom.getQueueKafkaTopic());

        return webClient.post()
                .uri(QUEUE_SERVICE_URL + "/api/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoCreateQueue)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new QueueCreationException(classroom.getId())))
                .bodyToMono(DtoQueue.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof RuntimeException))
                .timeout(Duration.ofSeconds(5))
                .subscribe(
                        product -> logger.info("Queue created successfully {}", product.getId()),
                        error -> logger.error("Error when creating queue for classroom {}: {}", classroom.getId(), error.getMessage())
                );
    }

    public Disposable deleteQueue(String roomId) {

        return webClient.delete()
                .uri(QUEUE_SERVICE_URL + "/api/queue?roomId=" + roomId)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new QueueDeletionException(roomId)))
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof RuntimeException))
                .timeout(Duration.ofSeconds(5))
                .subscribe(
                        voidd -> logger.info("Queue deleted successfully for classroom {}", roomId),
                        error -> logger.error("Error when deleting queue for classroom {}: {}", roomId, error.getMessage())
                );
    }
}