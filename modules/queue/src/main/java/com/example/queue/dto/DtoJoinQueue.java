package com.example.queue.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DtoJoinQueue {

    private String userId;
    private String queueId;
    private String email;
    private String name;
}
