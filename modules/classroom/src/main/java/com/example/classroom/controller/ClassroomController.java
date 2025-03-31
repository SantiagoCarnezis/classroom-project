package com.example.classroom.controller;

import com.example.classroom.dto.DtoCreateClassroom;
import com.example.classroom.entity.Classroom;
import com.example.classroom.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClassroomController {

    private final ClassroomService classroomService;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody DtoCreateClassroom dto)
    {
        Classroom body = classroomService.createRoom(dto);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id)
    {
        classroomService.deleteRoom(id);
        String message = messageSource.getMessage("classroom.deleted", null, Locale.ENGLISH);
        return new ResponseEntity(message, HttpStatus.OK);
    }

    @PutMapping("/pause/{roomId}")
    public ResponseEntity pause(@PathVariable String roomId)
    {
        classroomService.pause(roomId);
        String message = messageSource.getMessage("classroom.pause", new Object[]{roomId}, Locale.ENGLISH);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/resume/{roomId}")
    public ResponseEntity resume(@PathVariable String roomId)
    {
        classroomService.resume(roomId);
        String message = messageSource.getMessage("classroom.resume", new Object[]{roomId}, Locale.ENGLISH);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/resume-all")
    public ResponseEntity resumeAllRooms()
    {
        classroomService.resumeAllRooms();
        String message = messageSource.getMessage("classroom.resume.all", null, Locale.ENGLISH);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllRooms() {
        List<Classroom> classrooms = classroomService.getAllRooms();
        return new ResponseEntity<>(classrooms, HttpStatus.OK);
    }
}
