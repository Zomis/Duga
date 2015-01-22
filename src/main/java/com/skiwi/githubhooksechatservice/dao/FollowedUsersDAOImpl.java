package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.FollowedUser;

@Repository
public class FollowedUsersDAOImpl implements FollowedUsersDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId) {
		try {
			Query query = openSession().createQuery("from FollowedUser user where user.name = :name");
			query.setParameter("name", name);
			FollowedUser repo = (FollowedUser) query.uniqueResult();
			if (repo != null) {
				repo.setLastChecked(lastChecked);
				repo.setLastEventId(lastEventId);
				openSession().merge(repo);
				return;
			}
			
			repo = new FollowedUser();
			repo.setName(name);
			repo.setLastChecked(lastChecked);
			repo.setLastEventId(lastEventId);
			repo.setRoomIds("");
			openSession().persist(repo);
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
		}
	}

	@Override
	public List<FollowedUser> getAll() {
		try {
			Query query = openSession().createQuery("from FollowedUser user");
			@SuppressWarnings("unchecked")
			List<FollowedUser> users = query.list();
			return users;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return new ArrayList<FollowedUser>();
		}
	}
	
}
