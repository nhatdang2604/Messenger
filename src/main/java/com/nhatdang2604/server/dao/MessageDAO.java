package com.nhatdang2604.server.dao;

import java.util.List;

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

	public Message find(Integer id) {
		
		Session session = factory.getCurrentSession();
		Message message = null;
		
		try {
			session.beginTransaction();
//			
//			message = session.get(Message.class, id);
//			
//			if (null != message) {
//			
//				String param = "id";
//				String query = 
//					"select smg " + 
//					"from " + Message.class.getName() + " msg " + 
//					"join fetch msg.room " +
//					"join fetch msg.user " + 
//					"join fetch msg.room.users " +
//					"where msg.id = :" + param;
//			
//				message = session
//						.createQuery(query, Message.class)
//						.setParameter(param, id)
//						.getSingleResult();
//			
//		
//			}
			
			String param = "id";
			String query = 
				"select msg " + 
				"from " + Message.class.getName() + " msg " + 
				"join fetch msg.room room " +
				"join fetch msg.user " + 
				"left join fetch room.users " +
				"where msg.id = :" + param;
			
			try {
				message = session
						.createQuery(query, Message.class)
						.setParameter(param, id)
						.getSingleResult();
			} catch (javax.persistence.NoResultException e) {
				message = null;
			}
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
