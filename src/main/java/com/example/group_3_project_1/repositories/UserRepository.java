package com.example.group_3_project_1.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.group_3_project_1.entities.Tweet;
import com.example.group_3_project_1.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long>{


    Optional<User> findByCredentials_Username(String username);
    Optional<User> findByCredentials_UsernameAndCredentials_Password(String username, String password);
    List<User> findByDeletedFalse();
    Optional<User> findByCredentialsUsername(String username);


}
