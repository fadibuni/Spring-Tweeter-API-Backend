package com.example.group_3_project_1.services.impl;


import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.exceptions.BadRequestException;
import com.example.group_3_project_1.mappers.TweetMapper;
import com.example.group_3_project_1.repositories.TweetRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.example.group_3_project_1.dtos.ContextDto;
import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetRequestDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.dtos.UserResponseDto;
import com.example.group_3_project_1.entities.Hashtag;
import com.example.group_3_project_1.entities.Tweet;
import com.example.group_3_project_1.entities.User;
import com.example.group_3_project_1.exceptions.NotAuthorizedException;
import com.example.group_3_project_1.exceptions.NotFoundException;
import com.example.group_3_project_1.mappers.HashtagMapper;
import com.example.group_3_project_1.mappers.TweetMapper;
import com.example.group_3_project_1.mappers.UserMapper;
import com.example.group_3_project_1.repositories.HashtagRepository;
import com.example.group_3_project_1.repositories.TweetRepository;
import com.example.group_3_project_1.repositories.UserRepository;
import com.example.group_3_project_1.services.TweetService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;

	/*
	 * Retrieves the users mentioned in the tweet with the given id. If that tweet
	 * is deleted or otherwise doesn't exist, an error should be sent in lieu of a
	 * response.
	 * 
	 * Deleted users should be excluded from the response.
	 * 
	 * IMPORTANT Remember that tags and mentions must be parsed by the server!
	 */
	@Override
	public List<UserResponseDto> getMentionedUsers(Long tweetId) {

		Tweet tweet = tweetRepository.findById(tweetId)
				.orElseThrow(() -> new NotFoundException("Tweet not found or deleted"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		return tweet.getMentionedUsers().stream().filter(user -> !user.isDeleted()).map(userMapper::entityToResponseDto)
				.collect(Collectors.toList());
	}

	/*
	 * Retrieves a tweet with a given id. If no such tweet exists, or the given
	 * tweet is deleted, an error should be sent in lieu of a response.
	 */
	@Override
	public TweetResponseDto getTweetById(Long id) {

		Tweet tweet = tweetRepository.findById(id).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet has been deleted");
		}

		return tweetMapper.entityToResponseDto(tweet);
	}

	/*
	 * Retrieves the active users who have liked the tweet with the given id. If
	 * that tweet is deleted or otherwise doesn't exist, an error should be sent in
	 * lieu of a response.
	 * 
	 * Deleted users should be excluded from the response.
	 */
	@Override
	public List<UserResponseDto> getUsersWhoLikedTweet(Long tweetId) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		return tweet.getLikedByUsers().stream().filter(user -> !user.isDeleted()).map(userMapper::entityToResponseDto)
				.collect(Collectors.toList());
	}

	/*
	 * Retrieves the context of the tweet with the given id. If that tweet is
	 * deleted or otherwise doesn't exist, an error should be sent in lieu of a
	 * response.
	 * 
	 * IMPORTANT: While deleted tweets should not be included in the before and
	 * after properties of the result, transitive replies should. What that means is
	 * that if a reply to the target of the context is deleted, but there's another
	 * reply to the deleted reply, the deleted reply should be excluded but the
	 * other reply should remain.
	 */
	@Override
	public ContextDto getTweetContext(Long tweetId) {

		Tweet targetTweet = tweetRepository.findById(tweetId)
				.orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (targetTweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		List<TweetResponseDto> beforeTweets = collectBeforeTweets(targetTweet);

		List<TweetResponseDto> afterTweets = collectAfterTweets(targetTweet);

		TweetResponseDto targetTweetDto = tweetMapper.entityToResponseDto(targetTweet);

		ContextDto context = new ContextDto();
		context.setTarget(targetTweetDto);
		context.setBefore(beforeTweets);
		context.setAfter(afterTweets);

		return context;
	}

	private List<TweetResponseDto> collectBeforeTweets(Tweet tweet) {
		Tweet current = tweet.getInReplyTo();
		return collectTweets(current, false);
	}

	private List<TweetResponseDto> collectAfterTweets(Tweet tweet) {
		return collectRepliesRecursively(tweet.getReplies());
	}

	private List<TweetResponseDto> collectRepliesRecursively(List<Tweet> replies) {
		return replies.stream().flatMap(reply -> {
			if (reply.isDeleted()) {

				return collectRepliesRecursively(reply.getReplies()).stream();
			}

			return List.of(tweetMapper.entityToResponseDto(reply)).stream().flatMap(t -> List.of(t).stream());
		}).collect(Collectors.toList());
	}

	private List<TweetResponseDto> collectTweets(Tweet tweet, boolean includeDeleted) {
		List<TweetResponseDto> result = new java.util.ArrayList<>();
		while (tweet != null) {
			if (!tweet.isDeleted() || includeDeleted) {
				result.add(tweetMapper.entityToResponseDto(tweet));
			}
			tweet = tweet.getInReplyTo();
		}
		return result;
	}

	/*
	 * Retrieves the direct reposts of the tweet with the given id. If that tweet is
	 * deleted or otherwise doesn't exist, an error should be sent in lieu of a
	 * response.
	 * 
	 * Deleted reposts of the tweet should be excluded from the response.
	 */
	@Override
	public List<TweetResponseDto> getDirectReposts(Long tweetId) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		return tweet.getReposts().stream().filter(repost -> !repost.isDeleted()).map(tweetMapper::entityToResponseDto)
				.collect(Collectors.toList());
	}

	/*
	 * Retrieves the direct replies to the tweet with the given id. If that tweet is
	 * deleted or otherwise doesn't exist, an error should be sent in lieu of a
	 * response.
	 * 
	 * Deleted replies to the tweet should be excluded from the response.
	 */
	@Override
	public List<TweetResponseDto> getDirectReplies(Long tweetId) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		return tweet.getReplies().stream().filter(reply -> !reply.isDeleted()).map(tweetMapper::entityToResponseDto)
				.collect(Collectors.toList());
	}

	/*
	 * Retrieves the tags associated with the tweet with the given id. If that tweet
	 * is deleted or otherwise doesn't exist, an error should be sent in lieu of a
	 * response.
	 * 
	 * IMPORTANT Remember that tags and mentions must be parsed by the server!
	 */
	@Override
	public List<HashtagDto> getHashtagsByTweetId(Long tweetId) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		return tweet.getHashtags().stream().map(hashtagMapper::entityToResponseDto).collect(Collectors.toList());
	}

	/*
	 * "Deletes" the tweet with the given id. If no such tweet exists or the
	 * provided credentials do not match author of the tweet, an error should be
	 * sent in lieu of a response. If a tweet is successfully "deleted", the
	 * response should contain the tweet data prior to deletion.
	 * 
	 * IMPORTANT: This action should not actually drop any records from the
	 * database! Instead, develop a way to keep track of "deleted" tweets so that
	 * even if a tweet is deleted, data with relationships to it (like replies and
	 * reposts) are still intact.
	 */
	@Override
	public TweetResponseDto deleteTweet(Long tweetId, CredentialsDto credentials) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is already deleted");
		}

		if (!tweet.getAuthor().getCredentials().getUsername().equals(credentials.getUsername())
				|| !tweet.getAuthor().getCredentials().getPassword().equals(credentials.getPassword())) {
			throw new NotAuthorizedException("Invalid credentials for deleting this tweet");
		}

		TweetResponseDto responseDto = tweetMapper.entityToResponseDto(tweet);

		tweet.setDeleted(true);
		tweetRepository.save(tweet);

		return responseDto;
	}

	/*
	 * Creates a "like" relationship between the tweet with the given id and the
	 * user whose credentials are provided by the request body. If the tweet is
	 * deleted or otherwise doesn't exist, or if the given credentials do not match
	 * an active user in the database, an error should be sent. Following successful
	 * completion of the operation, no response body is sent.
	 */
	@Override
	public void likeTweet(Long tweetId, CredentialsDto credentials) {

		Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (tweet.isDeleted()) {
			throw new NotFoundException("Tweet is deleted");
		}

		User user = userRepository.findByCredentialsUsername(credentials.getUsername())
				.orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

		if (!user.getCredentials().getPassword().equals(credentials.getPassword()) || user.isDeleted()) {
			throw new NotAuthorizedException("Invalid credentials or user is inactive");
		}

		if (!tweet.getLikedByUsers().contains(user)) {
			tweet.getLikedByUsers().add(user);
			user.getLikedTweets().add(tweet);
			userRepository.save(user);
			tweetRepository.save(tweet);
		}else {
			System.out.println("User '" + credentials.getUsername() + "' already liked tweet ID: " + tweetId);
		}
	}

	/*
	 * Creates a reply tweet to the tweet with the given id. The author of the
	 * newly-created tweet should match the credentials provided by the request
	 * body. If the given tweet is deleted or otherwise doesn't exist, or if the
	 * given credentials do not match an active user in the database, an error
	 * should be sent in lieu of a response.
	 * 
	 * Because this creates a reply tweet, content is not optional. Additionally,
	 * notice that the inReplyTo property is not provided by the request. The server
	 * must create that relationship.
	 * 
	 * The response should contain the newly-created tweet.
	 * 
	 * IMPORTANT: when a tweet with content is created, the server must process the
	 * tweet's content for @{username} mentions and #{hashtag} tags. There is no way
	 * to create hashtags or create mentions from the API, so this must be handled
	 * automatically!
	 */
	@Override
	public TweetResponseDto replyToTweet(Long tweetId, TweetRequestDto tweetRequestDto) {

		Tweet parentTweet = tweetRepository.findById(tweetId)
				.orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (parentTweet.isDeleted()) {
			throw new NotFoundException("Cannot reply to a deleted tweet");
		}

		User author = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername())
				.orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

		if (!author.getCredentials().getPassword().equals(tweetRequestDto.getCredentials().getPassword())
				|| author.isDeleted()) {
			throw new NotAuthorizedException("Invalid credentials or user is inactive");
		}

		Tweet replyTweet = new Tweet();
		replyTweet.setAuthor(author);
		replyTweet.setContent(tweetRequestDto.getContent());
		replyTweet.setInReplyTo(parentTweet);

		processMentionsAndHashtags(replyTweet);

		tweetRepository.save(replyTweet);

		return tweetMapper.entityToResponseDto(replyTweet);
	}

	private void processMentionsAndHashtags(Tweet tweet) {
		String content = tweet.getContent();

		List<User> mentionedUsers = userRepository.findAll().stream()
				.filter(user -> content.contains("@" + user.getCredentials().getUsername()) && !user.isDeleted())
				.collect(Collectors.toList());
		tweet.setMentionedUsers(mentionedUsers);

		List<Hashtag> hashtags = Arrays.stream(content.split("\\s+")).filter(word -> word.startsWith("#"))
				.map(tag -> hashtagRepository.findByLabel(tag.substring(1)).orElseGet(() -> {
					Hashtag newTag = new Hashtag();
					newTag.setLabel(tag.substring(1));
					return hashtagRepository.save(newTag);
				})).collect(Collectors.toList());
		tweet.setHashtags(hashtags);
	}

	/*
	 * Creates a repost of the tweet with the given id. The author of the repost
	 * should match the credentials provided in the request body. If the given tweet
	 * is deleted or otherwise doesn't exist, or the given credentials do not match
	 * an active user in the database, an error should be sent in lieu of a
	 * response.
	 * 
	 * Because this creates a repost tweet, content is not allowed. Additionally,
	 * notice that the repostOf property is not provided by the request. The server
	 * must create that relationship.
	 * 
	 * The response should contain the newly-created tweet.
	 */
	@Override
	public TweetResponseDto repostTweet(Long tweetId, CredentialsDto credentials) {

		Tweet originalTweet = tweetRepository.findById(tweetId)
				.orElseThrow(() -> new NotFoundException("Tweet not found"));

		if (originalTweet.isDeleted()) {
			throw new NotFoundException("Cannot repost a deleted tweet");
		}

		User author = userRepository.findByCredentialsUsername(credentials.getUsername())
				.orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

		if (!author.getCredentials().getPassword().equals(credentials.getPassword()) || author.isDeleted()) {
			throw new NotAuthorizedException("Invalid credentials or user is inactive");
		}

		Tweet repostTweet = new Tweet();
		repostTweet.setAuthor(author);
		repostTweet.setRepostOf(originalTweet);

		tweetRepository.save(repostTweet);

		return tweetMapper.entityToResponseDto(repostTweet);
	}

	/*
	 * Creates a new simple tweet, with the author set to the user identified by the
	 * credentials in the request body. If the given credentials do not match an
	 * active user in the database, an error should be sent in lieu of a response.
	 * 
	 * The response should contain the newly-created tweet.
	 * 
	 * Because this always creates a simple tweet, it must have a content property
	 * and may not have inReplyTo or repostOf properties.
	 * 
	 * IMPORTANT: when a tweet with content is created, the server must process the
	 * tweet's content for @{username} mentions and #{hashtag} tags. There is no way
	 * to create hashtags or create mentions from the API, so this must be handled
	 * automatically!
	 */
	@Override
	public TweetResponseDto createSimpleTweet(TweetRequestDto tweetRequestDto) {

		if (tweetRequestDto.getCredentials() == null
				|| tweetRequestDto.getContent() == null
				|| tweetRequestDto.getContent().isBlank()
				|| tweetRequestDto.getCredentials().getUsername() == null
				|| tweetRequestDto.getCredentials().getPassword() == null
				|| tweetRequestDto.getCredentials().getUsername().isBlank()
				|| tweetRequestDto.getCredentials().getPassword().isBlank() ) {
			throw new BadRequestException("Content or Credentials cannot be empty");
		}
		User author = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername())
				.orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));


		if (!author.getCredentials().getPassword().equals(tweetRequestDto.getCredentials().getPassword())
				|| author.isDeleted()) {
			throw new NotAuthorizedException("Invalid credentials or user is inactive");
		}

		Tweet newTweet = new Tweet();
		newTweet.setAuthor(author);
		newTweet.setContent(tweetRequestDto.getContent());

		processMentionsAndHashtags(newTweet);

		tweetRepository.save(newTweet);

		return tweetMapper.entityToResponseDto(newTweet);
	}

    @Override
    public List<TweetResponseDto> getAllTweets() {
        return  tweetMapper.entitiesToResponseDtos(tweetRepository.findByDeletedFalse());
    }
}
