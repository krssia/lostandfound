package com.mongo.lostfound.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document("items")
@CompoundIndexes({
        @CompoundIndex(name = "geo_index", def = "{'location':'2dsphere'}")
})
public class Item {
    private String id;
    private String contactInfo;
    private Date createdAt;
    private String description;
    private List<String> imageIds;
    @Field("location")
    private GeoJsonPoint location;
    private Date lostOrFoundDate;
    private String status;
    private String title;
    private String type;
    private String userId;
}
