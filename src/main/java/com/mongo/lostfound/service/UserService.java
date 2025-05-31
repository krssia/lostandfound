package com.mongo.lostfound.service;

import com.mongo.lostfound.dto.UserRegisterDTO;
import com.mongo.lostfound.dto.UserUpdateDTO;
import com.mongo.lostfound.entity.User;
import com.mongo.lostfound.mapper.UserMapper;
import com.mongo.lostfound.repository.ItemRepository;
import com.mongo.lostfound.repository.UserRepository;
import com.mongo.lostfound.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private final ItemRepository itemRepository;
    public UserVO register(UserRegisterDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setCreatedAt(new Date());
        user.setRole("user");

        User saved = userRepository.save(user);

        return userMapper.toVO(saved);
    }

    public UserVO getById(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userMapper.toVO(user);
        } else {
            return null;
        }
    }

    public UserVO getByPhone(String phone) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userMapper.toVO(user);
        } else {
            return null;
        }
    }

    public UserVO updateUser(String userId, UserUpdateDTO dto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());

        return userMapper.toVO(userRepository.save(user));
    }

    @Transactional
    public boolean deleteUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        if (user.getAvatar() != null) {
            imageService.deleteImage(user.getAvatar());
        }

        itemRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

        return true;
    }


    public UserVO uploadAvatar(String phone, MultipartFile file) throws IOException {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        String imageId = imageService.storeImage(file);
        user.setAvatar(imageId);
        User updatedUser = userRepository.save(user);

        return userMapper.toVO(updatedUser);
    }

}
