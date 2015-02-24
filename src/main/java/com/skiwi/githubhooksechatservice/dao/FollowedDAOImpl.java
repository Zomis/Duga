package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.Followed;

@Repository
public class FollowedDAOImpl implements FollowedDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId, boolean user) {
		try {
			Query query = openSession().createQuery("from Followed gitfollowed where gitfollowed.name = :name");
			query.setParameter("name", name);
			Followed followed = (Followed) query.uniqueResult();
			if (followed != null) {
				followed.setLastChecked(lastChecked);
				followed.setLastEventId(lastEventId);
				openSession().merge(followed);
				return;
			}
			
			followed = new Followed();
			followed.setName(name);
			followed.setFollowType(user ? 1 : 0);
			followed.setLastChecked(lastChecked);
			followed.setLastEventId(lastEventId);
			followed.setRoomIds("");
			openSession().persist(followed);
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
		}
	}

	@Override
	public List<Followed> getAll() {
		try {
			Query query = openSession().createQuery("from Followed gitfollowed");
			@SuppressWarnings("unchecked")
			List<Followed> repos = query.list();
			return repos;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return new ArrayList<Followed>();
		}
	}
	
}
