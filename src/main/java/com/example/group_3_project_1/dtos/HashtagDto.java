package com.example.group_3_project_1.dtos;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashtagDto {
	private String label;
    private Timestamp firstUsed;
    private Timestamp lastUsed;
}
