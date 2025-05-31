package com.mongo.lostfound.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "messages")
public class Messages {
    @Id
    private String id;
    private String fromUserId;
    private String toUserId;
    private String content;
    private Date sentAt;
    private boolean read;
}
