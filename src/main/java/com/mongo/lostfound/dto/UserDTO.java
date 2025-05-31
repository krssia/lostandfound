package com.mongo.lostfound.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    @NotNull
    private Integer userId;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;
    @Email
    private String email;
}
