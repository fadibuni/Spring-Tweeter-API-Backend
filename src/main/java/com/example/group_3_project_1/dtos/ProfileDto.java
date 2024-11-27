package com.example.group_3_project_1.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDto {
	private String email;
    private String firstName;
    private String lastName;
    private String phone;
}
