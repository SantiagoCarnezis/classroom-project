package com.example.classroom.exception;

public class ClassroomNotFoundException extends RuntimeException {

    public ClassroomNotFoundException(String id) {
        super("classroom " + id + " not found");
    }
}
