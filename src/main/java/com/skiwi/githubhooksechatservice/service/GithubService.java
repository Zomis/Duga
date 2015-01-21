package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.FollowedRepository;


public interface GithubService {
	
	List<FollowedRepository> getAll();
	void update(String name, long update, long eventId);

}
