package com.skiwi.githubhooksechatservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="followed_user")
public class FollowedUser {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	private long lastChecked;
	
	private long lastEventId;
	
	private String roomIds;
	
	public Integer getId() {
		return id;
	}
	
	public long getLastChecked() {
		return lastChecked;
	}
	
	public long getLastEventId() {
		return lastEventId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setLastChecked(long lastChecked) {
		this.lastChecked = lastChecked;
	}
	
	public void setLastEventId(long lastEventId) {
		this.lastEventId = lastEventId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRoomIds() {
		return roomIds;
	}
	
	public void setRoomIds(String roomIds) {
		this.roomIds = roomIds;
	}
	
}
