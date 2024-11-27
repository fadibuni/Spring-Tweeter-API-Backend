package com.example.group_3_project_1.controllers;


import java.util.List;

import com.example.group_3_project_1.entities.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.group_3_project_1.dtos.ContextDto;
import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetRequestDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserResponseDto;
import com.example.group_3_project_1.services.TweetService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {
	
	private final TweetService tweetService;


	@GetMapping
	public List<TweetResponseDto> getAllTweets() {
		return tweetService.getAllTweets();
	}

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentions(@PathVariable Long id) {
        return tweetService.getMentionedUsers(id);
    }
    
    @GetMapping("/{id}")
    public TweetResponseDto getTweetById(@PathVariable Long id) {
        return tweetService.getTweetById(id);
    }
    
    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getUsersWhoLikedTweet(@PathVariable Long id) {
        return tweetService.getUsersWhoLikedTweet(id);
    }
    
    @GetMapping("/{id}/context")
    public ContextDto getTweetContext(@PathVariable Long id) {
        return tweetService.getTweetContext(id);
    }
    
    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getDirectReposts(@PathVariable Long id) {
        return tweetService.getDirectReposts(id);
    }
    
    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getDirectReplies(@PathVariable Long id) {
        return tweetService.getDirectReplies(id);
    }
    
    @GetMapping("/{id}/tags")
    public List<HashtagDto> getHashtagsByTweetId(@PathVariable Long id) {
        return tweetService.getHashtagsByTweetId(id);
    }
    
    @DeleteMapping("/{id}")
    public TweetResponseDto deleteTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
        return tweetService.deleteTweet(id, credentials);
    }
    
    @PostMapping("/{id}/like")
    public void likeTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
        tweetService.likeTweet(id, credentials);
    }

    @PostMapping("/{id}/reply")
    public TweetResponseDto replyToTweet(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.replyToTweet(id, tweetRequestDto);
    }
    
    @PostMapping("/{id}/repost")
    public TweetResponseDto repostTweet(@PathVariable Long id, @RequestBody CredentialsDto credentials) {
        return tweetService.repostTweet(id, credentials);
    }
    
    @PostMapping
    public TweetResponseDto createSimpleTweet(@RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createSimpleTweet(tweetRequestDto);
    }



}
