package com.example.classroom.exception;

public class QueueCreationException extends RuntimeException {

    public QueueCreationException(String id) {
        super("could not create queue for classroom " + id);
    }
}
