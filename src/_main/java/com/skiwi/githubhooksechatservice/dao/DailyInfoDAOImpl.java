package com.skiwi.githubhooksechatservice.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
	public DailyInfo addCommits(String name, String url, int commits, int additions, int deletions) {
		return add(name, url, daily -> daily.addCommits(commits, additions, deletions));
	}
	
	@Override
	public DailyInfo addIssues(String name, String url, int opened, int closed, int comments) {
		return add(name, url, daily -> daily.addIssues(opened, closed, comments));
	}
	
	private DailyInfo add(String name, String url, Consumer<DailyInfo> perform) {
		try {
			Query query = openSession().createQuery("from DailyInfo daily where daily.name = :name");
			query.setParameter("name", name);
			List<?> dailyInfos = query.list();
			if (!dailyInfos.isEmpty()) {
				DailyInfo dailyInfo = (DailyInfo) dailyInfos.get(0);
				perform.accept(dailyInfo);
				openSession().merge(dailyInfo);
				return dailyInfo;
			}
			DailyInfo dailyInfo = new DailyInfo();
			dailyInfo.setName(name);
			dailyInfo.setUrl(url);
			perform.accept(dailyInfo);
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
			openSession().clear();
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
