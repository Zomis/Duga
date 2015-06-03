package com.skiwi.githubhooksechatservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.ConfigDAO;

@Service
@Transactional
public class ConfigServiceImpl implements ConfigService {
	
	@Autowired
	private ConfigDAO configDAO;

	@Override
	public String getConfig(String key, String defaultValue) {
		return configDAO.getConfig(key, defaultValue);
	}

	@Override
	public void setConfig(String key, String value) {
		configDAO.setConfig(key, value);
	}

}
