package com.example.group_3_project_1.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import com.example.group_3_project_1.embeddables.Credentials;
import com.example.group_3_project_1.embeddables.Profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@ToString
@Table(name = "user_table")
@Entity
@NoArgsConstructor
@Data
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	private Credentials credentials;

	@Embedded
	private Profile profile;

	@CreationTimestamp
	@Column(nullable = false)
	private Timestamp joined;

	private boolean deleted = false;

	@ToString.Exclude
	@OneToMany(mappedBy = "author")
	private List<Tweet> tweets;

	@ManyToMany
	@JoinTable(
			name = "user_likes", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "tweet_id")
			)
	private List<Tweet> likedTweets = new ArrayList<>();

	@ManyToMany(mappedBy = "mentionedUsers")
	private List<Tweet> mentionedTweets;

	@ManyToMany
	@JoinTable(name = "followers_following")
	private List<User> followers;

	@ManyToMany(mappedBy = "followers")
	private List<User> following;

}
