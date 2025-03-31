package com.example.queue.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Getter
@Setter
@NoArgsConstructor
public class Queue {

    @Id
    private String id;
    private String roomId;
    private boolean paused;
    private String kafkaTopic;

    public Queue(String roomId) {

        this.roomId = roomId;
        this.paused = false;
    }
}
