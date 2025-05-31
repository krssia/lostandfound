package com.mongo.lostfound.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private String fromUserId;
    private String toUserId;
    private String content;
}