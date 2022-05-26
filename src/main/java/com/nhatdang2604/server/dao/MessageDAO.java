package com.nhatdang2604.server.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum MessageDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private MessageDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}
	
	//Create a message
	public Message create(Message message) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			Integer id = (Integer) session.save(message);
			message = session.get(Message.class, id);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return message;
	}
	
}
