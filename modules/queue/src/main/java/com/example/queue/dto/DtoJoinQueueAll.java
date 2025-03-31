package com.example.queue.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DtoJoinQueueAll {

    private String queueId;
    private int amount;
}
