package com.example.group_3_project_1.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
}
