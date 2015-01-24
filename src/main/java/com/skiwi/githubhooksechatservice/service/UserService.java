package com.skiwi.githubhooksechatservice.service;

import com.skiwi.githubhooksechatservice.model.DugaUser;

public interface UserService {
	
	DugaUser getUser(String login);

	void createUser(String username, String password);

}
