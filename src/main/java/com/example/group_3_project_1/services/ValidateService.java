package com.example.group_3_project_1.services;

import java.util.List;

public interface ValidateService {

    boolean tagExists(String label);

    boolean usernameExists(String username);

    boolean usernameAvailable(String username);
}
