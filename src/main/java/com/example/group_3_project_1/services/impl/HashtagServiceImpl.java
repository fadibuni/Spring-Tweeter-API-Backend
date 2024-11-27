package com.example.group_3_project_1.services.impl;

import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.entities.Hashtag;
import com.example.group_3_project_1.entities.Tweet;
import com.example.group_3_project_1.exceptions.NotFoundException;
import com.example.group_3_project_1.mappers.HashtagMapper;
import com.example.group_3_project_1.mappers.TweetMapper;
import com.example.group_3_project_1.repositories.HashtagRepository;
//import com.example.group_3_project_1.repositories.TweetRepository;
import com.example.group_3_project_1.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;

	// private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	@Override
	public List<HashtagDto> getAllHashtags() {
		return hashtagMapper.entitiesToResponseDtos(hashtagRepository.findAll());
	}

	@Override
	public HashtagDto createHashtag(HashtagDto hashtagDto) {
		Hashtag hashtag = hashtagMapper.requestDtoToEntity(hashtagDto);
		hashtag.setFirstUsed(timestamp);
		hashtag.setLastUsed(timestamp);
		return hashtagMapper.entityToResponseDto(hashtagRepository.saveAndFlush(hashtag));
	}

	@Override
	public List<TweetResponseDto> getTweetsByHashtagLabel(String label) {

		Hashtag hashtag = findHashtagByLabel(label);

		return mapToTweetResponseDtos(hashtag);
	}

	private Hashtag findHashtagByLabel(String label) {
		return hashtagRepository.findByLabel(label)
				.orElseThrow(() -> new NotFoundException("Hashtag with label '" + label + "' not found"));
	}

	private List<TweetResponseDto> mapToTweetResponseDtos(Hashtag hashtag) {
		return hashtag.getTweets().stream().filter(this::isValidTweet).sorted(this::reverseChronologicalOrder)
				.map(tweetMapper::entityToResponseDto).collect(Collectors.toList());
	}

	private boolean isValidTweet(Tweet tweet) {
		return !tweet.isDeleted() && tweet.getContent().contains("#" + tweet.getHashtags().get(0).getLabel());
	}

	private int reverseChronologicalOrder(Tweet t1, Tweet t2) {
		return t2.getPosted().compareTo(t1.getPosted());
	}
}
