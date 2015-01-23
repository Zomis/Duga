package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.DailyInfoDAO;
import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.model.DailyInfo;

@Service
@Transactional
public class DailyServiceImpl implements DailyService {
	
	@Autowired
	private DailyInfoDAO dailyDAO;

	@Override
	public DailyInfo add(GithubRepository repository, int commits, int opened,
			int closed, int additions, int deletions) {
		return dailyDAO.add(repository.getFullName(), repository.getHtmlUrl(), commits, opened, closed, additions, deletions);
	}

	@Override
	public List<DailyInfo> getAndReset() {
		return dailyDAO.getAndReset();
	}

	@Override
	public List<DailyInfo> get() {
		return dailyDAO.get();
	}

	@Override
	public String getUrl(String fullNameGithubStyle) {
		return dailyDAO.getUrl(fullNameGithubStyle);
	}


}
