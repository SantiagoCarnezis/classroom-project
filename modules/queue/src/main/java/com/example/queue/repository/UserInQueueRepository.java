package com.example.queue.repository;

import com.example.queue.entity.UserInQueue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInQueueRepository extends MongoRepository<UserInQueue, String> {

    List<UserInQueue> findAllByQueueId( String queueId);

    void deleteById(String id);
}
