package com.example.queue.repository;

import com.example.queue.entity.Queue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QueueRepository extends MongoRepository<Queue, String> {

    Optional<Queue> findByRoomId(String roomId);
    void deleteByRoomId(String roomId);
}
