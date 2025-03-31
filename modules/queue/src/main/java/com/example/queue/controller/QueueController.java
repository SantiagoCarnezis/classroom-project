package com.example.queue.controller;

import com.example.queue.dto.DtoCreateQueue;
import com.example.queue.dto.DtoJoinQueue;
import com.example.queue.dto.DtoJoinQueueAll;
import com.example.queue.entity.Queue;
import com.example.queue.entity.UserInQueue;
import com.example.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class QueueController {

    private final QueueService queueService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<List<Queue>> getAllQueues() {
        List<Queue> queues = queueService.getAll();
        return new ResponseEntity<>(queues, HttpStatus.OK);
    }

    @GetMapping("/{queue_id}/users")
    public ResponseEntity<List<UserInQueue>> getAllUsersInQueue(@PathVariable String queue_id) {
        List<UserInQueue> users = queueService.getUserInQueue(queue_id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createQueue(@RequestBody DtoCreateQueue dto)
    {
        Queue queue = queueService.createQueue(dto);
        return new ResponseEntity<>(queue, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteQueue(@RequestParam(required = true) String roomId)
    {
        queueService.deleteQueue(roomId);
        String message = messageSource.getMessage("queue.deleted", null, Locale.ENGLISH);
        return new ResponseEntity(message, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@RequestBody DtoJoinQueue dto)
    {
        queueService.joinQueue(dto);
        String message = messageSource.getMessage("queue.join", new Object[]{dto.getEmail(), dto.getQueueId()}, Locale.ENGLISH);
        return new ResponseEntity(message, HttpStatus.OK);
    }

    @PostMapping("/join/multiple")
    public ResponseEntity<?> joinQueueMultipleUsers(@RequestBody DtoJoinQueueAll dto)
    {
        queueService.joinQueueMultipleUsers(dto);
        String message = messageSource.getMessage("queue.join.multiple", null, Locale.ENGLISH);
        return new ResponseEntity(message, HttpStatus.OK);
    }
}
