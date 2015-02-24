package com.skiwi.githubhooksechatservice.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.Role;

@Repository
public class RoleDAOImpl implements RoleDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Role getRole(int id) {
		Role role = (Role) openSession().load(Role.class, id);
		return role;
	}

	@Override
	public Role createOrGetRole(String roleName) {
		try {
			Query query = openSession().createQuery("from Role r where r.role = :role");
			query.setParameter("role", roleName);
			System.out.println("executing role query with parameter " + roleName);
			Role role = (Role) query.uniqueResult();
			if (role != null) {
				return role;
			}
			role = new Role();
			role.setRole(roleName);
			openSession().save(role);
			return role;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}

}
