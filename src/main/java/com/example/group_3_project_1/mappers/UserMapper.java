package com.example.group_3_project_1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.group_3_project_1.dtos.UserRequestDto;
import com.example.group_3_project_1.dtos.UserResponseDto;
import com.example.group_3_project_1.entities.User;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {
	
	@Mapping(target = "username", source = "credentials.username")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "joined", source = "joined")
    UserResponseDto entityToResponseDto(User entity);
    
    User requestDtoToEntity(UserRequestDto request);
    
    List<UserResponseDto> entitiesToResponseDtos(List<User> entities);
}