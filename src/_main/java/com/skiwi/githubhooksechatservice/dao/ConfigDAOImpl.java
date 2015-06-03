package com.skiwi.githubhooksechatservice.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.DugaConfig;

@Repository
public class ConfigDAOImpl implements ConfigDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public String getConfig(String key, String defaultValue) {
		try {
			Query query = openSession().createQuery("from DugaConfig conf where conf.key = :key");
			query.setParameter("key", key);
			DugaConfig role = (DugaConfig) query.uniqueResult();
			if (role == null) {
				role = new DugaConfig();
				role.setKey(key);
				role.setValue(defaultValue);
				openSession().save(role);
			}
			return role.getValue();
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void setConfig(String key, String value) {
		try {
			Query query = openSession().createQuery("from DugaConfig conf where conf.key = :key");
			query.setParameter("key", key);
			DugaConfig role = (DugaConfig) query.uniqueResult();
			role.setValue(value);
			openSession().merge(role);
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
		}
	}
	
}
