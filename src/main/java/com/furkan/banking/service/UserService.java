package com.furkan.banking.service;

import com.furkan.banking.exception.CustomException;
import com.furkan.banking.model.User;
import com.furkan.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkByUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }

    public boolean checkByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkByEmailAndIdNot(String email, UUID userId) {
        return userRepository.existsByEmailAndIdNot(email, userId);
    }

    public void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (Exception ex) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while saving user.");
        }
    }
}
