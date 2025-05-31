package com.mongo.lostfound.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MessageVO {
    private String id;
    private String fromUserId;
    private String toUserId;
    private String content;
    private Date sentAt;
    private boolean read;
}
