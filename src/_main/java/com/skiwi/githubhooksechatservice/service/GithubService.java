package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.Followed;


public interface GithubService {
	
	List<Followed> getAll();
	void update(String name, long update, long eventId, boolean user);

}
