package com.example.group_3_project_1.services;

import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;

import java.util.List;


public interface HashtagService {
    List<HashtagDto> getAllHashtags();

    HashtagDto createHashtag(HashtagDto hashtagDto);
    
    List<TweetResponseDto> getTweetsByHashtagLabel(String label);
    
}
