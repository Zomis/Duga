package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.FollowedRepoDAO;
import com.skiwi.githubhooksechatservice.model.FollowedRepository;

@Service
@Transactional
public class GithubServiceImpl implements GithubService {
	
	@Autowired
	private FollowedRepoDAO repoDAO;

	@Override
	public List<FollowedRepository> getAll() {
		return repoDAO.getAll();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId) {
		repoDAO.update(name, lastChecked, lastEventId);
	}

}
