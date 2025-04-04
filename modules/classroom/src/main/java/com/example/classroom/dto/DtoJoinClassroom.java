package com.example.classroom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DtoJoinClassroom {

    private String roomId;
    private String userId;
    private String email;
    private String name;
}
