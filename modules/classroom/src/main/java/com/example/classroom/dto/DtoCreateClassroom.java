package com.example.classroom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DtoCreateClassroom {

    private String name;
    private int maxCapacity;
    private int duration; //seconds
}
