package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.FollowedRepository;


public interface FollowedRepoDAO {
	
	void update(String name, long lastChecked, long lastEventId);
	List<FollowedRepository> getAll();

}
