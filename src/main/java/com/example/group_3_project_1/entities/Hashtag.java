package com.example.group_3_project_1.entities;

import java.sql.Timestamp;
import java.util.List;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@ToString
@Entity
@NoArgsConstructor
@Data
public class Hashtag {
    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @CreationTimestamp
    private Timestamp firstUsed;

    @UpdateTimestamp
    private Timestamp lastUsed;

    @ToString.Exclude
    @ManyToMany(mappedBy = "hashtags")
    private List<Tweet> tweets;

}
