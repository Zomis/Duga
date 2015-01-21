package com.skiwi.githubhooksechatservice.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.model.DugaUser;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public DugaUser getUser(String login) {
		try {
			Query query = openSession().createQuery("from User u where u.login = :login");
			query.setParameter("login", login);
			System.out.println("executing query with parameter" + login);
			return (DugaUser) query.uniqueResult();
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public DugaUser createUser(String login, String password) {
		DugaUser user = getUser(login);
		if (user != null) {
			return null;
		}
		
		try {
			user = new DugaUser();
			user.setLogin(login);
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(roleDAO.createOrGetRole("MODERATOR"));
			openSession().save(user);
			return user;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}

}
