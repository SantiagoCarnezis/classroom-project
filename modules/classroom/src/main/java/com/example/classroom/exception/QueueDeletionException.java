package com.example.classroom.exception;

public class QueueDeletionException extends RuntimeException {

    public QueueDeletionException(String roomId) {
        super("could not delete queue for classroom " + roomId);
    }
}
