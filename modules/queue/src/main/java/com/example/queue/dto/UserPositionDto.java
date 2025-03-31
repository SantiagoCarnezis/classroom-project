package com.example.queue.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserPositionDto {

    private String email;
    private String name;
    private LocalDateTime joiningDate;
    private int andIncrement;
}
