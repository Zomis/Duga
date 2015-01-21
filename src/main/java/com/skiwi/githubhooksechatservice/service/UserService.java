package com.skiwi.githubhooksechatservice.service;

import com.skiwi.githubhooksechatservice.model.DugaUser;

public interface UserService {
	
	public DugaUser getUser(String login);

}
