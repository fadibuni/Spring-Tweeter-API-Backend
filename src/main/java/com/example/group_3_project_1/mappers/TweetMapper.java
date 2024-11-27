package com.example.group_3_project_1.mappers;

import com.example.group_3_project_1.dtos.TweetRequestDto;
import com.example.group_3_project_1.dtos.TweetResponseDto;
import com.example.group_3_project_1.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {
	Tweet requestDtoToEntity(TweetRequestDto request);

	TweetResponseDto entityToResponseDto(Tweet tweet);

	List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> entities);
}
