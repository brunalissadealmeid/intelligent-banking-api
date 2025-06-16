package com.example.intelligentbanking.service;

import com.example.intelligentbanking.dto.UserDto;
import com.example.intelligentbanking.user.User;
import com.example.intelligentbanking.repository.UserRepository;
import com.example.intelligentbanking.exception.UserAlreadyExistsException; // Will create this custom exception
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username " + userDto.getUsername() + " already exists.");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + userDto.getEmail() + " already exists.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // createdAt is set by @PrePersist in User entity

        User savedUser = userRepository.save(user);

        return convertToDto(savedUser);
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    // Helper method to convert DTO to Entity (optional, can be done in service method)
    // private User convertToEntity(UserDto userDto) {
    //     User user = new User();
    //     user.setUsername(userDto.getUsername());
    //     user.setEmail(userDto.getEmail());
    //     return user;
    // }
}
