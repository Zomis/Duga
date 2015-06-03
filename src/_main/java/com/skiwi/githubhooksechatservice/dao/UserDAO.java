package com.skiwi.githubhooksechatservice.dao;

import com.skiwi.githubhooksechatservice.model.DugaUser;

public interface UserDAO {
	
	public DugaUser getUser(String login);

	DugaUser createUser(String login, String password);

}
