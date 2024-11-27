package com.example.group_3_project_1.mappers;

import com.example.group_3_project_1.dtos.ProfileDto;
import com.example.group_3_project_1.embeddables.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile dtoToEntity(ProfileDto profileDto);
    ProfileDto entityToDto(Profile profile);


}
