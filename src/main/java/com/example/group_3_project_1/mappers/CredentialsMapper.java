package com.example.group_3_project_1.mappers;

import com.example.group_3_project_1.dtos.CredentialsDto;
import com.example.group_3_project_1.embeddables.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
    Credentials requestDtoToEntity(CredentialsDto credentialsRequestDto);
    CredentialsDto entityToResponseDto(Credentials credentials);
}
