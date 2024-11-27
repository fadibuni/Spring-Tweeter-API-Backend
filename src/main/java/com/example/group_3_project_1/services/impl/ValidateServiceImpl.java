package com.example.group_3_project_1.services.impl;

import com.example.group_3_project_1.entities.Hashtag;
import com.example.group_3_project_1.entities.User;
import com.example.group_3_project_1.repositories.HashtagRepository;
import com.example.group_3_project_1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import com.example.group_3_project_1.services.ValidateService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService{
    private final HashtagRepository hashtagRepository;
    private final UserRepository userRepository;

    @Override
    public boolean tagExists(String label) {
        return hashtagRepository.findByLabel(label).isPresent();
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByCredentials_Username(username).isPresent();
    }

    @Override
    public boolean usernameAvailable(String username) {
        return userRepository.findByCredentials_Username(username).isEmpty();
    }
}
