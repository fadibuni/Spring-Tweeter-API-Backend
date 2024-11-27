package com.example.group_3_project_1.services;

import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserRequestDto;
import com.example.group_3_project_1.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUser(String username);

    UserResponseDto deleteUser(String username, CredentialsDto credentialsDto);

    void followUser(String username, CredentialsDto credentialsDto);

    void unfollowUser(String username, CredentialsDto credentialsDto);

    List<UserResponseDto> getFollowers(String username);

    List<UserResponseDto> getFollowing(String username);

    List<TweetResponseDto> getTweets(String username);

    List<TweetResponseDto> getFeed(String username);

    List<TweetResponseDto> getMentions(String username);

    UserResponseDto updateUser(String username, UserRequestDto userRequestDto);
}
