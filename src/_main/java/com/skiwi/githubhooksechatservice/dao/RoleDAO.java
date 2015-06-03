package com.skiwi.githubhooksechatservice.dao;

import com.skiwi.githubhooksechatservice.model.Role;

public interface RoleDAO {
	
	Role getRole(int id);

	Role createOrGetRole(String string);

}
