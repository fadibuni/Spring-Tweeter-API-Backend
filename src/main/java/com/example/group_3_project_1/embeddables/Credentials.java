package com.example.group_3_project_1.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Credentials {

	@Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
}
