package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.Followed;


public interface FollowedDAO {
	
	void update(String name, long lastChecked, long lastEventId, boolean user);
	List<Followed> getAll();

}
