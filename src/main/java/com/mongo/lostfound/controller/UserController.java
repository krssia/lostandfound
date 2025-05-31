package com.mongo.lostfound.controller;

import com.mongo.lostfound.Response.Response;
import com.mongo.lostfound.dto.UserRegisterDTO;
import com.mongo.lostfound.dto.UserUpdateDTO;
import com.mongo.lostfound.service.UserService;
import com.mongo.lostfound.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response<UserVO>> register(@RequestBody @Valid UserRegisterDTO dto) {
        UserVO userVO = userService.register(dto);
        return ResponseEntity.ok(new Response<>(true, userVO, "注册成功"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserVO>> getById(@PathVariable String id) {
        UserVO userVO = userService.getById(id);
        if (userVO != null) {
            return ResponseEntity.ok(new Response<>(true, userVO, "查询成功"));
        } else {
            return ResponseEntity.ok(new Response<>(false, null, "用户不存在"));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Response<UserVO>> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateDTO dto) {

        UserVO vo = userService.updateUser(userId, dto);
        return vo != null ?
                ResponseEntity.ok(new Response<>(true, vo, "更新成功")) :
                ResponseEntity.ok(new Response<>(false, null, "用户不存在"));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable String userId) {

        boolean success = userService.deleteUser(userId);
        return success ?
                ResponseEntity.ok(new Response<>(true, null, "删除成功")) :
                ResponseEntity.ok(new Response<>(false, null, "用户不存在"));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<Response<UserVO>> getByPhone(@PathVariable String phone) {
        UserVO userVO = userService.getByPhone(phone);
        if (userVO != null) {
            return ResponseEntity.ok(new Response<>(true, userVO, "查询成功"));
        } else {
            return ResponseEntity.ok(new Response<>(false, null, "用户不存在"));
        }
    }

    @PostMapping("/phone/{phone}/avatar")
    public ResponseEntity<Response<UserVO>> uploadAvatar(@PathVariable String phone, @RequestParam("file") MultipartFile file) throws IOException {
        UserVO userVO = userService.uploadAvatar(phone, file);
        if (userVO != null) {
            return ResponseEntity.ok(new Response<>(true, userVO, "头像上传成功"));
        } else {
            return ResponseEntity.ok(new Response<>(false, null, "用户不存在"));
        }
    }
}