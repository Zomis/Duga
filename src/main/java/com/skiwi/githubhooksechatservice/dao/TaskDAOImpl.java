package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.TaskData;

@Repository
public class TaskDAOImpl implements TaskDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<TaskData> getTasks() {
		try {
			Query query = openSession().createQuery("from TaskData task");
			@SuppressWarnings("unchecked")
			List<TaskData> tasks = (List<TaskData>) query.list();
			return new ArrayList<TaskData>(tasks);
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}
	
}
