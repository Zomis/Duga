package com.skiwi.githubhooksechatservice.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RuntimeLogServiceImpl implements RuntimeLogService {
	
    private final static Logger logger = Logger.getLogger(RuntimeLogServiceImpl.class.getSimpleName());
	
    // TODO: Make it possible to read the messages at runtime
    
	@Override
	public void log(String tag, String message) {
		logger.info(tag + ": " + message);
	}

	@Override
	public void log(String message) {
		log("no-tag", message);
	}

}
