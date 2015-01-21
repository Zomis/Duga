package com.skiwi.githubhooksechatservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.UserDAO;
import com.skiwi.githubhooksechatservice.model.DugaUser;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDAO;

	public DugaUser getUser(String login) {
		return userDAO.getUser(login);
	}

}
