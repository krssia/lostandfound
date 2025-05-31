package com.mongo.lostfound.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Date;
import java.util.List;

@Data
public class ItemVO {
    private String id;
    private String contactInfo;
    private Date createdAt;
    private String description;
    private GeoJsonPoint location;
    private Date lostOrFoundDate;
    private String status;
    private String title;
    private String type;
    private String userId;
    private List<String> imageIds;
}
