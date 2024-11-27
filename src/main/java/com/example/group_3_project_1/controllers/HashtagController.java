package com.example.group_3_project_1.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class HashtagController {
    
	private final HashtagService hashtagService;
    
    @GetMapping
    public List<HashtagDto> getAllHashtags() {
        return hashtagService.getAllHashtags();
    }

    

    @PostMapping
    public HashtagDto createHashtag(@RequestBody HashtagDto hashtagDto) {
        return hashtagService.createHashtag(hashtagDto);
    }

    
    @GetMapping("/{label}")
    public List<TweetResponseDto> getTweetsByHashtagLabel(@PathVariable String label) {
        return hashtagService.getTweetsByHashtagLabel(label);
    }

}
