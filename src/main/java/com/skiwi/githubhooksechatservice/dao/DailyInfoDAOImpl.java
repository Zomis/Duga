package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skiwi.githubhooksechatservice.model.DailyInfo;

@Repository
public class DailyInfoDAOImpl implements DailyInfoDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public DailyInfo add(String name, String url, int commits, int opened, int closed, int additions, int deletions) {
		try {
			Query query = openSession().createQuery("from DailyInfo daily where daily.name = :name");
			query.setParameter("name", name);
			DailyInfo dailyInfo = (DailyInfo) query.uniqueResult();
			if (dailyInfo != null) {
				dailyInfo.add(commits, opened, closed, additions, deletions);
				openSession().merge(dailyInfo);
				return dailyInfo;
			}
			dailyInfo = new DailyInfo();
			dailyInfo.setName(name);
			dailyInfo.setUrl(url);
			dailyInfo.add(commits, opened, closed, additions, deletions);
			openSession().save(dailyInfo);
			return dailyInfo;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<DailyInfo> getAndReset() {
		try {
			Query query = openSession().createQuery("from DailyInfo daily");
			Query deleteQuery = openSession().createQuery("delete from DailyInfo daily");
			
			@SuppressWarnings("unchecked")
			List<DailyInfo> repos = query.list();
			deleteQuery.executeUpdate();
			return repos;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<DailyInfo> get() {
		try {
			Query query = openSession().createQuery("from DailyInfo daily");
			@SuppressWarnings("unchecked")
			List<DailyInfo> repos = query.list();
			return repos;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public String getUrl(String fullNameGithubStyle) {
		try {
			Query query = openSession().createQuery("from DailyInfo daily where daily.name = :name");
			query.setString("name", fullNameGithubStyle);
			
			DailyInfo info = (DailyInfo) query.uniqueResult();
			return info != null ? info.getUrl() : null;
		}
		catch (Exception ex) {
			System.out.println("BIG FAT ERROR: " + ex);
			ex.printStackTrace();
			return null;
		}
	}

}
