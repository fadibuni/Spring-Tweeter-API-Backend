package com.example.group_3_project_1.controllers;

import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserRequestDto;
import com.example.group_3_project_1.dtos.UserResponseDto;
import com.example.group_3_project_1.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    //@ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/@{username}")
    public UserResponseDto getUserByUsername(@PathVariable String username) {
        return userService.getUser(username);
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getFollowers(@PathVariable String username) {
        return userService.getFollowers(username);
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getFollowing(@PathVariable String username) {
        return userService.getFollowing(username);
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/@{username}/tweets")
    public List<TweetResponseDto> getTweets(@PathVariable String username) {
        return userService.getTweets(username);
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getMentions(@PathVariable String username) {
        return userService.getMentions(username);
    }

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getFeed(@PathVariable String username) {
        return userService.getFeed(username);
    }
    //@ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        System.out.println("UserController.createUser: " + userRequestDto);
        return userService.createUser(userRequestDto);
    }

    //@ResponseStatus(HttpStatus.OK)
    @PostMapping("/@{username}/follow")
    public void followUser(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        userService.followUser(username, credentialsDto);
    }

    //@ResponseStatus(HttpStatus.OK)
    @PostMapping("/@{username}/unfollow")
    public void unfollowUser(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        userService.unfollowUser(username, credentialsDto);
    }

    //ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUser(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        return userService.deleteUser(username, credentialsDto);
    }

    //@ResponseStatus(HttpStatus.OK)
    @PatchMapping("/@{username}")
    public UserResponseDto updateUser(@PathVariable String username, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(username, userRequestDto);
    }

}
