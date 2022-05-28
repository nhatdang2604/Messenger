package com.nhatdang2604.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.nhatdang2604.server.entities.User;
import com.nhatdang2604.server.utils.HibernateUtil;

public enum UserDAO {
	
	INSTANCE;
	
	private SessionFactory factory;
	
	private UserDAO() {
		factory = HibernateUtil.INSTANCE.getSessionFactory();
	}
	
	public User getUserByUsername(String username) {

		Session session = factory.getCurrentSession();
		
		User user = null;
		
		try {
			session.beginTransaction();
			
			//Parameterize the query
			String param = "username";
			
			//Make the query
			String query = "from " + User.class.getName() + " u where u.username = :" + param;
			
			user = (User) session.createQuery(query)
					.setParameter(param, username)
					.setMaxResults(1)
					.stream()
					.findFirst()
					.orElse(null);
			
			if (null != user) {
				Hibernate.initialize(user.getRooms());
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return user;
	}
	
	//Create a client account
	public User create(User client) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			
			Integer id = (Integer) session.save(client);
			client = session.get(User.class, id);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return client;
	}
	
	//Create a client account
	public User update(User client) {
		Session session = factory.getCurrentSession();
			
		try {
			session.beginTransaction();
				
			session.update(client);
				
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
			
		return client;
	}

	public User find(Integer id) {
		
		Session session = factory.getCurrentSession();
		User client = null;
		
		try {
			session.beginTransaction();
				
			client = session.get(User.class, id);
				
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
			
		return client;
		
		
	}

	public List<User> findAll() {
		
		Session session = factory.getCurrentSession();
		
		List<User> users = new ArrayList<>();

		try {
			session.beginTransaction();
			
			users = session.createQuery("from " + User.class.getName()).list();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.getTransaction().commit();
			session.close();
		}
		
		return users;
		
	}
		
}
