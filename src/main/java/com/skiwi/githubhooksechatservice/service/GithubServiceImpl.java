package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.FollowedDAO;
import com.skiwi.githubhooksechatservice.model.Followed;

@Service
@Transactional
public class GithubServiceImpl implements GithubService {
	
	@Autowired
	private FollowedDAO followedDAO;

	@Override
	public List<Followed> getAll() {
		return followedDAO.getAll();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId, boolean user) {
		followedDAO.update(name, lastChecked, lastEventId, user);
	}

}
