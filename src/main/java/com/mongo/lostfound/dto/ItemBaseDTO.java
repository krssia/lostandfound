package com.mongo.lostfound.dto;

import java.util.Date;

public interface ItemBaseDTO {
    String getContactInfo();
    String getDescription();
    String getTitle();
    String getType();
    String getUserId();
    String getStatus();
    Date getLostOrFoundDate();
}
