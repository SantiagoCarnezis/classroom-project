package com.example.classroom.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DtoCreateQueue {

    private String roomId;
    private String kafkaTopic;
}