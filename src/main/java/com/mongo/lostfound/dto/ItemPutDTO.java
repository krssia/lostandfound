package com.mongo.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class ItemPutDTO implements ItemBaseDTO {
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid contact number format")
    private String contactInfo;
    private String description;

    @DecimalMin(value = "-180.0", inclusive = true, message = "经度不能小于 -180")
    @DecimalMax(value = "180.0", inclusive = true, message = "经度不能大于 180")
    private Double longitude;

    @DecimalMin(value = "-90.0", inclusive = true, message = "纬度不能小于 -90")
    @DecimalMax(value = "90.0", inclusive = true, message = "纬度不能大于 90")
    private Double latitude;

    @Schema(example = "1995-07-22T21:41:30.000+08:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date lostOrFoundDate;

    @Schema(hidden = true)
    private List<String> imageIds;

    @Schema(description = "上传的图片文件列表")
    private List<MultipartFile> images;

    @Pattern(regexp = "^(已认领|未认领)$")
    private String status;

    private String title;

    @Pattern(regexp = "^(lost|found)$", message = "\"type must be either 'lost' or 'found'\"")
    private String type;
    private String userId;
}