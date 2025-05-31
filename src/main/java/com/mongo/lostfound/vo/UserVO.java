package com.mongo.lostfound.vo;

import lombok.Data;

@Data
public class UserVO {
    private String id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private String role;
}
