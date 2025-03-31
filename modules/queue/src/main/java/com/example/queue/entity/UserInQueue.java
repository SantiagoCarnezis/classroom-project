package com.example.queue.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@NoArgsConstructor
public class UserInQueue {

    @Id
    private String id;
    private String email;
    private String name;
    private String queueId;
    private LocalDateTime joiningDate;
}
