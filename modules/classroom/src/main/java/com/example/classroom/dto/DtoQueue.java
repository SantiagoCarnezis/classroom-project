package com.example.classroom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DtoQueue {

    private String id;
    private String roomId;
    private boolean paused;
    private String kafkaTopic;
}