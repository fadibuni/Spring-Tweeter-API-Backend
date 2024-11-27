package com.example.group_3_project_1.services;

import com.example.group_3_project_1.dtos.TweetResponseDto;

import java.util.List;

import com.example.group_3_project_1.dtos.ContextDto;
import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetRequestDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserResponseDto;

public interface TweetService {
	
	List<UserResponseDto> getMentionedUsers(Long tweetId);
	
	TweetResponseDto getTweetById(Long id);
	
	List<UserResponseDto> getUsersWhoLikedTweet(Long tweetId);
	
	ContextDto getTweetContext(Long tweetId);
	
	List<TweetResponseDto> getDirectReposts(Long tweetId);
	
	List<TweetResponseDto> getDirectReplies(Long tweetId);
	
	List<HashtagDto> getHashtagsByTweetId(Long tweetId);
	
	TweetResponseDto deleteTweet(Long tweetId, CredentialsDto credentials);
	
	void likeTweet(Long tweetId, CredentialsDto credentials);
	
	TweetResponseDto replyToTweet(Long tweetId, TweetRequestDto tweetRequestDto);
	
	TweetResponseDto repostTweet(Long tweetId, CredentialsDto credentials);
	
	TweetResponseDto createSimpleTweet(TweetRequestDto tweetRequestDto);

    List<TweetResponseDto> getAllTweets();

}
