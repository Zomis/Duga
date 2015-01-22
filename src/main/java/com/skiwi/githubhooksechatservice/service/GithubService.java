package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.FollowedRepository;
import com.skiwi.githubhooksechatservice.model.FollowedUser;


public interface GithubService {
	
	List<FollowedRepository> getAll();
	void update(String name, long update, long eventId);

	List<FollowedUser> getAllUsers();
	void updateUser(String name, long update, long eventId);

}
