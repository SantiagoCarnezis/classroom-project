package com.example.classroom;

import com.example.classroom.dto.DtoCreateClassroom;
import com.example.classroom.entity.Classroom;
import com.example.classroom.repository.ClassroomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class ClassroomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassroomRepository classroomRepository;

    @Test
    public void createRoom_shouldReturnRoomDetails_whenRequestIsValid() throws Exception {

        DtoCreateClassroom dto = new DtoCreateClassroom();
        dto.setName("Room1");
        dto.setMaxCapacity(30);
        dto.setDuration(10);

        Classroom classroom = new Classroom();
        classroom.setId("roomId123");
        classroom.setName(dto.getName());
        classroom.setMaxCapacity(dto.getMaxCapacity());
        classroom.setDuration(dto.getDuration());
        classroom.setPaused(false);
        classroom.setUsers(new ArrayList<>());
        classroom.setQueueKafkaTopic("topic");

        Mockito.when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/classroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("roomId123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Room1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maxCapacity").value(30))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(10));


        ArgumentCaptor<Classroom> captor = ArgumentCaptor.forClass(Classroom.class);
        Mockito.verify(classroomRepository, times(2)).save(captor.capture());

        Classroom capturedDto = captor.getValue();

        assertEquals(capturedDto.getId(), "roomId123");
        assertEquals(capturedDto.getName(), "Room1");
        assertEquals(capturedDto.getMaxCapacity(), 30);
        assertEquals(capturedDto.getDuration(), 10);
    }
}

