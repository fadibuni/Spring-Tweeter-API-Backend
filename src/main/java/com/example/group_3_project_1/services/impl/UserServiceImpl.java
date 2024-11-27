package com.example.group_3_project_1.services.impl;

import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserRequestDto;
import com.example.group_3_project_1.dtos.UserResponseDto;
import com.example.group_3_project_1.entities.Tweet;
import com.example.group_3_project_1.entities.User;
import com.example.group_3_project_1.exceptions.BadRequestException;
import com.example.group_3_project_1.exceptions.NotAuthorizedException;
import com.example.group_3_project_1.exceptions.NotFoundException;
import com.example.group_3_project_1.mappers.CredentialsMapper;
import com.example.group_3_project_1.mappers.TweetMapper;
import com.example.group_3_project_1.mappers.UserMapper;
import com.example.group_3_project_1.repositories.UserRepository;
import com.example.group_3_project_1.services.ValidateService;
import org.springframework.stereotype.Service;

import com.example.group_3_project_1.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    private final CredentialsMapper credentialsMapper;
    private final ValidateService validateService;
    private final TweetMapper tweetMapper;

    private boolean isValideUserRequest(UserRequestDto userRequestDto) {
        return userRequestDto != null
                && userRequestDto.getCredentials() != null
                && userRequestDto.getProfile() != null
                && userRequestDto.getCredentials().getUsername() != null
                && userRequestDto.getCredentials().getPassword() != null
                && userRequestDto.getProfile().getEmail() != null;
    }
    private boolean isValideCredentials(CredentialsDto credentialsDto) {
        return  credentialsDto != null
                && credentialsDto.getUsername() != null
                && credentialsDto.getPassword() != null;
    }
    private boolean isValideUsername(String username) {
        return username != null && !username.isBlank();
    }

    private boolean isValideCredentialsAndUsername(CredentialsDto credentialsDto, String username) {
        return isValideCredentials(credentialsDto)
                && isValideUsername(username);
    }
    private boolean isValideUserRequestDtoAndName(CredentialsDto credentialsDto, String username) {
        return isValideCredentials(credentialsDto)
                && isValideUsername(username)
                && username.equals(credentialsDto.getUsername());
    }
    private boolean isValideUserRequestDtoAndName(UserRequestDto userRequestDto, String username) {
        return  userRequestDto != null
                && userRequestDto.getCredentials() != null
                && userRequestDto.getProfile() != null
                && isValideUserRequestDtoAndName(userRequestDto.getCredentials(), username);
    }
    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByCredentials_Username(username);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return user.get();
    }

    private User getUserByUsernameAndPassword(String username, String password) {
        Optional<User> user = userRepository.findByCredentials_UsernameAndCredentials_Password(username, password);
        if (user.isEmpty()) {
            throw new NotAuthorizedException("No user matched with the given credentials");
        }
        return user.get();
    }
    private List<User> filteredUsers(List<User> users) {
        return users.stream().filter(user -> !user.isDeleted()).toList();
    }
    private List<Tweet> filteredTweets(List<Tweet> tweets) {
        return tweets.stream().filter(tweet -> !tweet.isDeleted()).toList();
    }
    private List<Tweet> sortedTweetsByReverseChronologicalOrder(List<Tweet> tweets) {
        return tweets.stream().sorted((t1, t2) -> t2.getPosted().compareTo(t1.getPosted())).toList();
    }
    private List<Tweet> getFilteredAndSortedTweets(List<Tweet> tweets) {
        return sortedTweetsByReverseChronologicalOrder(filteredTweets(tweets));
    }
    private User updatesUser(User user, UserRequestDto userRequestDto) {
        if (userRequestDto.getProfile().getEmail() != null) {
            user.getProfile().setEmail(userRequestDto.getProfile().getEmail());
        }
        if (userRequestDto.getProfile().getFirstName() != null) {
            user.getProfile().setFirstName(userRequestDto.getProfile().getFirstName());
        }
        if (userRequestDto.getProfile().getLastName() != null) {
            user.getProfile().setLastName(userRequestDto.getProfile().getLastName());
        }
        if (userRequestDto.getProfile().getPhone() != null) {
            user.getProfile().setPhone(userRequestDto.getProfile().getPhone());
        }
        return user;
    }
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findByDeletedFalse());
    }

    @Override
    public UserResponseDto getUser(String username) {
        return userMapper.entityToResponseDto(getUserByUsername(username));
    }

    @Override
    public List<UserResponseDto> getFollowers(String username) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        User user = getUserByUsername(username);
        List<User> activeFollowers = filteredUsers(user.getFollowers());
        return userMapper.entitiesToResponseDtos(activeFollowers);
    }

    @Override
    public List<UserResponseDto> getFollowing(String username) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        return userMapper.entitiesToResponseDtos(filteredUsers(getUserByUsername(username).getFollowing()));
    }

    @Override
    public List<TweetResponseDto> getTweets(String username) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        return tweetMapper.entitiesToResponseDtos(getFilteredAndSortedTweets(getUserByUsername(username).getTweets()));
    }

    @Override
    public List<TweetResponseDto> getFeed(String username) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        User user = getUserByUsername(username);
        List<Tweet> allTweets = user.getTweets();
        user.getFollowing().forEach(followingUser -> allTweets.addAll(followingUser.getTweets()));
        return tweetMapper.entitiesToResponseDtos(getFilteredAndSortedTweets(allTweets));
    }

    @Override
    public List<TweetResponseDto> getMentions(String username) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        return tweetMapper.entitiesToResponseDtos(getFilteredAndSortedTweets(getUserByUsername(username).getMentionedTweets()));
    }

    /**
     Updates the profile of a user with the given username. If no such user exists, the user is deleted,
     or the provided credentials do not match the user, an error should be sent in lieu of a response.
     In the case of a successful update, the returned user should contain the updated data.
     */
    @Override
    public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
        if (!isValideUsername(username)) {
            throw new BadRequestException("Invalid request, username is missing");
        }
        if (!validateService.usernameExists(username)) {
            throw new NotFoundException("Username not found");
        }
        if (!isValideUserRequestDtoAndName(userRequestDto, username)) {
            throw new BadRequestException("Invalid request, some info is missing or username in " +
                    "request body does not match the username in the path");
        }
        User userInRepo = getUserByUsernameAndPassword(userRequestDto.getCredentials().getUsername(), userRequestDto.getCredentials().getPassword());
        if (userInRepo.isDeleted()) {
            throw new BadRequestException("User already deleted");
        }
        return userMapper.entityToResponseDto(userRepository.saveAndFlush(updatesUser(userInRepo, userRequestDto)));
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (!isValideUserRequest(userRequestDto)) {
            throw new BadRequestException("Invalid request, some info is missing");
        }
        if (!isValideCredentials(userRequestDto.getCredentials())) {
            throw new BadRequestException("Invalid request, credentials are missing");
        }
        Optional<User> optionalUser = userRepository.findByCredentials_Username(userRequestDto.getCredentials().getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isDeleted()) {
                throw new BadRequestException("User already exists");
            }
            else {
                user.setDeleted(false);
                userRepository.saveAndFlush(user);
                return userMapper.entityToResponseDto(user);
            }
        }
        return userMapper.entityToResponseDto(userRepository.saveAndFlush(userMapper.requestDtoToEntity(userRequestDto)));
    }
    @Override
    public void followUser(String username, CredentialsDto credentialsDto) {
        if (!isValideCredentialsAndUsername(credentialsDto, username)) {
            throw new BadRequestException("Invalid request, user credentials or username to follow is missing");
        }
        User loggedInUser = getUserByUsernameAndPassword(credentialsDto.getUsername(), credentialsDto.getPassword());
        User userToFollow = getUserByUsername(username);
        if (userToFollow.isDeleted()) {
            throw new BadRequestException("User to follow is deleted");
        }
        if (loggedInUser.getFollowing().contains(userToFollow)) {
            throw new BadRequestException("User already following the user");
        }
        userToFollow.getFollowers().add(loggedInUser);
        userRepository.saveAndFlush(userToFollow);
    }

    @Override
    public void unfollowUser(String username, CredentialsDto credentialsDto) {
        if (!isValideCredentialsAndUsername(credentialsDto, username)) {
            throw new BadRequestException("Invalid request, user credentials or username to unfollow is missing");
        }
        User loggedInUser = getUserByUsernameAndPassword(credentialsDto.getUsername(), credentialsDto.getPassword());
        User userToUnfollow = getUserByUsername(username);
        if (userToUnfollow.isDeleted()) {
            throw new BadRequestException("User to unfollow is deleted");
        }
        if (!loggedInUser.getFollowing().contains(userToUnfollow)) {
            throw new BadRequestException("User already not following the user");
        }
        userToUnfollow.getFollowers().remove(loggedInUser);
        userRepository.saveAndFlush(userToUnfollow);
    }


    @Override
    public UserResponseDto deleteUser(String username, CredentialsDto credentialsDto) {
        if (!isValideUserRequestDtoAndName(credentialsDto, username)) {
            throw new BadRequestException("Invalid request, some info is missing or username in " +
                    "request body does not match the username in the path");
        }
        User user = getUserByUsername(username);
        if (!user.getCredentials().getUsername().equals(credentialsDto.getUsername()) || !user.getCredentials().getPassword().equals(credentialsDto.getPassword())) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        if (user.isDeleted()) {
            throw new BadRequestException("User already deleted");
        }
        user.setDeleted(true);
        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));
    }



}
