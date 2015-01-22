package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.FollowedRepoDAO;
import com.skiwi.githubhooksechatservice.dao.FollowedUsersDAO;
import com.skiwi.githubhooksechatservice.model.FollowedRepository;
import com.skiwi.githubhooksechatservice.model.FollowedUser;

@Service
@Transactional
public class GithubServiceImpl implements GithubService {
	
	@Autowired
	private FollowedRepoDAO repoDAO;

	@Autowired
	private FollowedUsersDAO userDAO;

	@Override
	public List<FollowedRepository> getAll() {
		return repoDAO.getAll();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId) {
		repoDAO.update(name, lastChecked, lastEventId);
	}

	@Override
	public List<FollowedUser> getAllUsers() {
		return userDAO.getAll();
	}

	@Override
	public void updateUser(String name, long lastChecked, long lastEventId) {
		userDAO.update(name, lastChecked, lastEventId);
	}

}
