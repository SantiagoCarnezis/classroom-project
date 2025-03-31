package com.example.classroom.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
public class Classroom {

    @Id
    private String id;
    private String name;
    private List<User> users;
    private String queueKafkaTopic;
    private boolean paused;
    private int maxCapacity;
    private int duration; //seconds

    public void addUser(User user) {
        users.add(user);
    }

    public void removeAll() {
        users = new ArrayList<>();
    }

    public boolean isFull() {
        return this.getUsers().size() >= this.getMaxCapacity();
    }
}
