package com.example.group_3_project_1.mappers;

import com.example.group_3_project_1.dtos.HashtagDto;
import com.example.group_3_project_1.entities.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
	Hashtag requestDtoToEntity(HashtagDto hashtagDto);

	HashtagDto entityToResponseDto(Hashtag entity);

	List<HashtagDto> entitiesToResponseDtos(List<Hashtag> entities);
}
