package com.skiwi.githubhooksechatservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="duga_config")
public class DugaConfig {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String key;
	
	private String value;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
