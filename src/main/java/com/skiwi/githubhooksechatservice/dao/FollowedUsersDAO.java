package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.FollowedUser;


public interface FollowedUsersDAO {
	
	void update(String name, long lastChecked, long lastEventId);
	List<FollowedUser> getAll();

}
