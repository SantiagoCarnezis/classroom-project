package com.example.classroom.repository;


import com.example.classroom.entity.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends MongoRepository<Classroom, String> {

    Optional<Classroom> findByName(String name);
}
