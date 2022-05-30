package com.nhatdang2604.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.entities.FileInfo;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum FileDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private FileDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}
	
	//Update a file info
	public FileInfo update(FileInfo info) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			session.update(info);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return info;
	}

	public FileInfo find(Integer id) {
		Session session = factory.getCurrentSession();
		FileInfo info = null;
		
		try {
			session.beginTransaction();
			info = session.get(FileInfo.class, id);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return info;
	}


	
}
