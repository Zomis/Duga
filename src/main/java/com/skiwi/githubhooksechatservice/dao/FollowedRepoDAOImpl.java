package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.FollowedRepository;

@Repository
public class FollowedRepoDAOImpl implements FollowedRepoDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void update(String name, long lastChecked, long lastEventId) {
		try {
			Query query = openSession().createQuery("from FollowedRepository repo where repo.name = :name");
			query.setParameter("name", name);
			FollowedRepository repo = (FollowedRepository) query.uniqueResult();
			if (repo != null) {
				repo.setLastChecked(lastChecked);
				repo.setLastEventId(lastEventId);
				openSession().merge(repo);
				return;
			}
			
			repo = new FollowedRepository();
			repo.setName(name);
			repo.setLastChecked(lastChecked);
			repo.setLastEventId(lastEventId);
			openSession().persist(repo);
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
		}
	}

	@Override
	public List<FollowedRepository> getAll() {
		try {
			Query query = openSession().createQuery("from FollowedRepository repo");
			@SuppressWarnings("unchecked")
			List<FollowedRepository> repos = query.list();
			return repos;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return new ArrayList<FollowedRepository>();
		}
	}
	
}
