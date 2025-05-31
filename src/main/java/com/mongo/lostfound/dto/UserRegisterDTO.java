package com.mongo.lostfound.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
@Data
public class UserRegisterDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;
    @Email
    private String email;
}