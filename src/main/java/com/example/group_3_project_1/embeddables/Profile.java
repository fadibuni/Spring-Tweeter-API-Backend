package com.example.group_3_project_1.embeddables;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Embeddable
@Data
@NoArgsConstructor // Lombok generates a no-argument constructor
public class Profile {
    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nonnull
    private String email;

    @Nullable
    private String phone;
}
