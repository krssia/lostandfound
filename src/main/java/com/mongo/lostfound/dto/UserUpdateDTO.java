package com.mongo.lostfound.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;
    @Email
    private String email;
    private String avatar;
}
