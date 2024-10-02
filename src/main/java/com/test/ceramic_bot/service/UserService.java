package com.test.ceramic_bot.service;

import com.test.ceramic_bot.model.Role;
import com.test.ceramic_bot.model.dto.UserDto;
import com.test.ceramic_bot.model.entity.UserEntity;
import com.test.ceramic_bot.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    @Transactional
    public UserDto addUser(Long telegramId, String name, Role role) {
        try {
            UserEntity savedEntity = userRepository.save(UserEntity.of(telegramId, name, role));
            return UserDto.from(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with this telegram Id already exists!", e);
        }
    }

    public Optional<UserDto> getUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).map(UserDto::from);
    }

    @Transactional
    public  UserDto addAdmin(Long telegramId, String name) {
        Optional<UserEntity> existingUser = userRepository.findByTelegramId(telegramId);
        if(existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            if(user.getRole() == Role.ROLE_ADMIN) {
                throw new IllegalArgumentException("이 사용자가 이미 관리자로 승급되었습니다.");
            }
            user.setRole(Role.ROLE_ADMIN);      // 역할을 ADMIN으로 변경
            userRepository.save(user);
            return UserDto.from(user);
        } else {
            // 새로운 관리자를 추가
            return addUser(telegramId, name, Role.ROLE_ADMIN);
        }
    }

    @Transactional
    public  UserDto removeAdmin(Long telegramId) {
        Optional<UserEntity> existingUser = userRepository.findByTelegramId(telegramId);
        if(existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            if(user.getRole() != Role.ROLE_ADMIN) {
                throw new IllegalArgumentException("This user is not an admin.");
            }
            user.setRole(Role.ROLE_USER);      // 역할을 User으로 변경
            userRepository.save(user);
            return UserDto.from(user);
        } else {
            throw new NotFoundException("User not found");
        }
    }
}
